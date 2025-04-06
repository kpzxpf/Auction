package com.volzhin.auction.service;

import com.volzhin.auction.config.filter.UserContext;
import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.entity.bid.Bid;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.producer.NewBidProducer;
import com.volzhin.auction.repository.BidRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidMapper bidMapper;
    private final BidRepository bidRepository;
    private final LotService lotService;
    private final UserService userService;
    private final NewBidProducer newBidProducer;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserContext userContext;

    @Transactional
    public void addBid(BidDto bidDto) {
        Bid bid = convertBidDtoToBid(bidDto);
        Lot lot = lotService.findById(bidDto.getLotId());

        if (lot.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Auction ended");
        }

        if (bid.getAmount().compareTo(lot.getCurrentPrice()) <= 0) {
            throw new RuntimeException("The bid is too low");
        }

        bidRepository.save(bid);
        lot.setCurrentPrice(bid.getAmount());
        lotService.save(lot);

        newBidProducer.send(bidMapper.toCache(bidDto));

        messagingTemplate.convertAndSend("/topic/lots/" + lot.getId(), lot.getCurrentPrice());
    }

    public List<Bid> getBidsByLotId(long lotId) {
        return bidRepository.getBidByLotId(lotId);
    }

    public List<Bid> getBidsByUserId(long userId) {
        return bidRepository.findUserMaxBidsLot(userId);
    }

    private Bid convertBidDtoToBid(BidDto bidDto) {
        return Bid.builder()
                .id(bidDto.getId())
                .amount(bidDto.getAmount())
                .user(userService.getUserById(userContext.getCurrentUserId()))
                .lot(lotService.findById(bidDto.getLotId()))
                .build();
    }
}
