package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.LotService;
import com.volzhin.auction.service.image.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {
    private final LotService lotService;
    private final LotMapper lotMapper;
    private final ImageService imageService;

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
        LotDto lotDto = lotMapper.toDto(lotService.findById(id));
        lotDto.setImageUrls(imageService.getImageUrlsByLotId(lotDto.getId()));

        return lotDto;
    }

    @GetMapping("user/{userId}")
    public List<LotDto> getLotsByUserId(@PathVariable Long userId) {
        List<LotDto> lotDtos = lotMapper.toDto(lotService.getLotsByUserId(userId));
        lotDtos.forEach(lotDto -> {lotDto.setImageUrls(imageService.getImageUrlsByLotId(lotDto.getId()));});

        return lotDtos;
    }

    @DeleteMapping("/{id}")
    public void deleteLotById(@PathVariable Long id) {
        lotService.deleteLot(id);
    }
}