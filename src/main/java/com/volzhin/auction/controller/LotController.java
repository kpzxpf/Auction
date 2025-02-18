package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {
    private final LotService lotService;
    private final LotMapper lotMapper;

    @PostMapping
    public LotDto createLot(@Valid @RequestBody LotDto lot) {
        Lot savedLot =  lotService.createLot(lot);
        return lotMapper.toDto(savedLot);
    }

    @PutMapping
    public LotDto updateLot(LotDto lot) {
        Lot savedLot =  lotService.updateLot(lot);
        return lotMapper.toDto(savedLot);
    }

    @GetMapping("/{id}")
    public LotDto getLotById(@PathVariable Long id) {
        return lotMapper.toDto(lotService.findById(id));
    }

    @GetMapping
    public Slice<LotDto> getLots(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return lotMapper.toDto(lotService.getLots(page, size));
    }
}
