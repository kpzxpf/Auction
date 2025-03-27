package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<LotDto> getLots(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "9") int size,
                                @RequestParam(required = false) String categoryName) {
        return feedService.getLotFeed(page, size, categoryName);
    }
}
