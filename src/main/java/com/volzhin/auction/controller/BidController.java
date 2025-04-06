package com.volzhin.auction.controller;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.mapper.BidMapper;
import com.volzhin.auction.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;
    private final BidMapper bidMapper;

    @PostMapping
    public void addBid(@RequestBody BidDto bidDto) {
        bidService.addBid(bidDto);
    }

    @GetMapping("/lot/{lotId}")
    public List<BidDto> getBidsByLotId(@PathVariable Long lotId) {
        return bidMapper.toDto(bidService.getBidsByLotId(lotId));
    }

    @GetMapping("/user/{userId}")
    public List<BidDto> getBidsByUserId(@PathVariable long userId) {
        return bidMapper.toDto(bidService.getBidsByUserId(userId));
    }
}
