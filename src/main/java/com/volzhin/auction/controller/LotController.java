package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {
    private final LotService lotService;
    private final LotMapper lotMapper;

    @PostMapping
    public LotDto createLot(
            @Valid @ModelAttribute LotDto lot,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Lot savedLot = lotService.createLot(lot, files);
        return lotMapper.toDto(savedLot);
    }

    @PutMapping
    public LotDto updateLot(@RequestBody LotDto lot) {
        Lot updatedLot = lotService.updateLot(lot);
        return lotMapper.toDto(updatedLot);
    }

    @GetMapping("/{id}")
    public LotDto getLotById(@PathVariable Long id) {
        return lotMapper.toDto(lotService.findById(id));
    }

    @GetMapping("user/{userId}")
    public List<LotDto> getLotsByUserId(@PathVariable Long userId) {
        return lotMapper.toDto(lotService.getLotsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public void deleteLotById(@PathVariable Long id) {
        lotService.deleteLot(id);
    }
}