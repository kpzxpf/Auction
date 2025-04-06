package com.volzhin.auction.controller;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;


    @PostMapping
    public void addBid(@RequestBody BidDto bidDto) {
        bidService.addBid(bidDto);
    }

    @GetMapping("/lot/{lotId}")
    public List<BidDto> getBidsByLotId(@PathVariable Long lotId) {
        return bidService.getBidsByLotId(lotId).stream().map(bid ->
                BidDto.builder().id(bid.getId())
                        .lotId(bid.getLot().getId())
                        .amount(bid.getAmount())
                        .bidTime(bid.getCreatedAt())
                        .userId(bid.getUser().getId())
                        .build()).toList();
    }
}
