package com.volzhin.auction.service;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.entity.bid.Bid;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.producer.NewBidProducer;
import com.volzhin.auction.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final BidMapper bidMapper;
    private final BidRepository bidRepository;
    private final LotService lotService;
    private final UserService userService;
    private final NewBidProducer newBidProducer;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void addBid(BidDto bidDto) {
        String currentUsername = getCurrentUsername();
        User currentUser = userService.getUserByUsername(currentUsername);

        Bid bid = convertBidDtoToBid(bidDto, currentUser);
        Lot lot = lotService.findById(bidDto.getLotId());

        if (lot.getStatus() != Lot.Status.active || lot.getEndTime().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to bid on ended or inactive lot ID: {}", lot.getId());
            throw new RuntimeException("Auction ended or inactive");
        }
        if (lot.getSeller().getId().equals(currentUser.getId())) {
            log.warn("Seller {} attempted to bid on their own lot ID: {}", currentUsername, lot.getId());
            throw new RuntimeException("Seller cannot bid on their own lot");
        }

        if (bid.getAmount().compareTo(lot.getCurrentPrice()) <= 0) {
            log.warn("Bid amount {} is too low for lot ID: {}. Current price: {}",
                    bid.getAmount(), lot.getId(), lot.getCurrentPrice());
            throw new RuntimeException("The bid is too low");
        }

        bidRepository.save(bid);
        lot.setCurrentPrice(bid.getAmount());
        lotService.save(lot);

        newBidProducer.send(bidMapper.toCache(bidDto));

        messagingTemplate.convertAndSend("/topic/lots/" + lot.getId(), lot.getCurrentPrice());
        log.info("User '{}' placed a bid of {} on lot ID: {}", currentUsername, bid.getAmount(), lot.getId());
    }

    public List<Bid> getBidsByLotId(long lotId) {
        return bidRepository.getBidByLotId(lotId);
    }

    public List<Bid> getBidsByUserId(long userId) {
        return bidRepository.findUserMaxBidsLot(userId);
    }


    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            log.error("Could not get username from principal: {}", principal);
            throw new IllegalStateException("User not authenticated");
        }
    }

    private Bid convertBidDtoToBid(BidDto bidDto, User user) {
        return Bid.builder()
                .amount(bidDto.getAmount())
                .user(user)
                .lot(lotService.findById(bidDto.getLotId()))
                .build();
    }
}
