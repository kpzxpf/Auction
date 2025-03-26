package com.volzhin.auction.service;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.producer.NewBidProducer;
import com.volzhin.auction.repository.BidRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidService {

    private final NewBidProducer newBidProducer;
    private final BidMapper bidMapper;
    private final BidRepository bidRepository;

    @Transactional
    public void addBid(BidDto bidDto) {
        bidRepository.save(bidMapper.toEntity(bidDto));

        newBidProducer.send(bidMapper.toCache(bidDto));
    }
}
