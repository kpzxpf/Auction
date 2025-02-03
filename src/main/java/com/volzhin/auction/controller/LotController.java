package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lot")
@RequiredArgsConstructor
public class LotController {
    private final LotService lotService;
    private final LotMapper lotMapper;

    @PostMapping
    public LotDto createLot(LotDto lot) {
        Lot savedLot =  lotService.createLot(lotMapper.toEntity(lot));
        return lotMapper.toDto(savedLot);
    }

    @PutMapping
    public LotDto updateLot(LotDto lot) {
        Lot savedLot =  lotService.updateLot(lotMapper.toEntity(lot));
        return lotMapper.toDto(savedLot);
    }

    @GetMapping
    public LotDto getLotById(long id) {
        return lotMapper.toDto(lotService.findById(id));
    }
}
