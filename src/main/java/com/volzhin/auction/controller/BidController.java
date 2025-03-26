package com.volzhin.auction.controller;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;
    private final BidMapper bidMapper;

    @PostMapping
    public void addBid(@RequestBody BidDto bidDto) {
        bidService.addBid(bidDto);
    }
}
