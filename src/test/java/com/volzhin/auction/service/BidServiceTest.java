package com.volzhin.auction.service;

import com.volzhin.auction.config.filter.UserContext;
import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.entity.bid.Bid;
import com.volzhin.auction.entity.bid.BidCache;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.producer.NewBidProducer;
import com.volzhin.auction.repository.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BidServiceTest {

    @Mock
    private BidMapper bidMapper;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private LotService lotService;
    @Mock
    private UserService userService;
    @Mock
    private NewBidProducer newBidProducer;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private BidService bidService;

    @Captor
    private ArgumentCaptor<Bid> bidCaptor;
    @Captor
    private ArgumentCaptor<Lot> lotCaptor;
    @Captor
    private ArgumentCaptor<BidCache> bidCacheCaptor;
    @Captor
    private ArgumentCaptor<String> topicCaptor;
    @Captor
    private ArgumentCaptor<BigDecimal> priceCaptor;

    private final long CURRENT_USER_ID = 5L;
    private final long LOT_ID = 10L;

    @BeforeEach
    void setUp() {
        lenient().when(userContext.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
    }

    @Test
    void addBid_shouldSaveBidUpdateLotSendEvents_whenBidIsValid() {
        BigDecimal bidAmount = new BigDecimal("150.00");
        BigDecimal currentPrice = new BigDecimal("100.00");
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        BidDto bidDto = BidDto.builder().lotId(LOT_ID).amount(bidAmount).build();
        User bidder = User.builder().id(CURRENT_USER_ID).build();
        Lot lot = Lot.builder().id(LOT_ID).currentPrice(currentPrice).endTime(endTime).build();
        Bid savedBid = Bid.builder().id(25L).amount(bidAmount).user(bidder).lot(lot).build();
        BidCache bidCache = BidCache.builder().id(25L).lotId(LOT_ID).userId(CURRENT_USER_ID).amount(bidAmount).build();

        when(lotService.findById(LOT_ID)).thenReturn(lot);
        when(userService.getUserById(CURRENT_USER_ID)).thenReturn(bidder);
        when(bidRepository.save(any())).thenReturn(savedBid);
        when(bidMapper.toCache(bidDto)).thenReturn(bidCache);

        bidService.addBid(bidDto);

        verify(userContext).getCurrentUserId();
        verify(userService).getUserById(CURRENT_USER_ID);
        verify(bidMapper).toCache(bidDto);
        verify(lotService, times(2)).findById(LOT_ID);

        verify(bidRepository).save(bidCaptor.capture());
        Bid capturedBid = bidCaptor.getValue();
        assertEquals(0L, capturedBid.getId());
        assertEquals(bidAmount, capturedBid.getAmount());
        assertEquals(bidder, capturedBid.getUser());
        assertEquals(lot, capturedBid.getLot());

        verify(lotService).save(lotCaptor.capture());
        assertEquals(bidAmount, lotCaptor.getValue().getCurrentPrice());

        verify(newBidProducer).send(bidCacheCaptor.capture());
        assertSame(bidCache, bidCacheCaptor.getValue());

        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), priceCaptor.capture());
        assertEquals("/topic/lots/" + LOT_ID, topicCaptor.getValue());
        assertEquals(bidAmount, priceCaptor.getValue());

        verifyNoMoreInteractions(lotService, bidRepository, newBidProducer, messagingTemplate);
    }

    @Test
    void addBid_shouldThrowRuntimeException_whenAuctionEnded() {
        BigDecimal bidAmount = new BigDecimal("150.00");
        BidDto bidDto = BidDto.builder().lotId(LOT_ID).amount(bidAmount).build();
        User bidder = User.builder().id(CURRENT_USER_ID).build();
        Lot lot = Lot.builder().id(LOT_ID).currentPrice(new BigDecimal("100.00")).endTime(LocalDateTime.now().minusMinutes(1)).build();

        when(lotService.findById(LOT_ID)).thenReturn(lot);
        when(userService.getUserById(CURRENT_USER_ID)).thenReturn(bidder);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidService.addBid(bidDto));
        assertEquals("Auction ended", ex.getMessage());

        verify(lotService, times(2)).findById(LOT_ID);
        verifyNoInteractions(bidRepository, newBidProducer, messagingTemplate, bidMapper);
        verify(lotService, never()).save(any());
    }

    @Test
    void addBid_shouldThrowRuntimeException_whenBidIsTooLow() {
        BigDecimal bidAmount = new BigDecimal("90.00");
        BidDto bidDto = BidDto.builder().lotId(LOT_ID).amount(bidAmount).build();
        User bidder = User.builder().id(CURRENT_USER_ID).build();
        Lot lot = Lot.builder().id(LOT_ID).currentPrice(new BigDecimal("100.00")).endTime(LocalDateTime.now().plusHours(1)).build();

        when(lotService.findById(LOT_ID)).thenReturn(lot);
        when(userService.getUserById(CURRENT_USER_ID)).thenReturn(bidder);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidService.addBid(bidDto));
        assertEquals("The bid is too low", ex.getMessage());

        verify(lotService, times(2)).findById(LOT_ID);
        verifyNoInteractions(bidRepository, newBidProducer, messagingTemplate, bidMapper);
        verify(lotService, never()).save(any());
    }

    @Test
    void addBid_shouldThrowRuntimeException_whenBidIsEqualToCurrent() {
        BigDecimal bidAmount = new BigDecimal("100.00");
        BidDto bidDto = BidDto.builder().lotId(LOT_ID).amount(bidAmount).build();
        User bidder = User.builder().id(CURRENT_USER_ID).build();
        Lot lot = Lot.builder().id(LOT_ID).currentPrice(new BigDecimal("100.00")).endTime(LocalDateTime.now().plusHours(1)).build();

        when(lotService.findById(LOT_ID)).thenReturn(lot);
        when(userService.getUserById(CURRENT_USER_ID)).thenReturn(bidder);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidService.addBid(bidDto));
        assertEquals("The bid is too low", ex.getMessage());

        verify(lotService, times(2)).findById(LOT_ID);
        verifyNoInteractions(bidRepository, newBidProducer, messagingTemplate, bidMapper);
        verify(lotService, never()).save(any());
    }

    @Test
    void getBidsByLotId_shouldReturnBids() {
        long specificLotId = 20L;
        Bid bid1 = Bid.builder().id(1L).amount(BigDecimal.TEN).lot(Lot.builder().id(specificLotId).build()).build();
        Bid bid2 = Bid.builder().id(2L).amount(BigDecimal.ONE).lot(Lot.builder().id(specificLotId).build()).build();
        List<Bid> expected = List.of(bid1, bid2);

        when(bidRepository.getBidByLotId(specificLotId)).thenReturn(expected);

        List<Bid> actual = bidService.getBidsByLotId(specificLotId);

        assertEquals(expected, actual);
        verify(bidRepository).getBidByLotId(specificLotId);
        verifyNoMoreInteractions(bidRepository);
    }

    @Test
    void getBidsByUserId_shouldReturnBids() {
        long specificUserId = 30L;
        Bid bid1 = Bid.builder().id(3L).amount(BigDecimal.TEN).user(User.builder().id(specificUserId).build()).build();
        Bid bid2 = Bid.builder().id(4L).amount(BigDecimal.ONE).user(User.builder().id(specificUserId).build()).build();
        List<Bid> expected = List.of(bid1, bid2);

        when(bidRepository.findUserMaxBidsLot(specificUserId)).thenReturn(expected);

        List<Bid> actual = bidService.getBidsByUserId(specificUserId);

        assertEquals(expected, actual);
        verify(bidRepository).findUserMaxBidsLot(specificUserId);
        verifyNoMoreInteractions(bidRepository);
    }
}