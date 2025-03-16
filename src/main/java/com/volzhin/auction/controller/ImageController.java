package com.volzhin.auction.controller;

import com.volzhin.auction.dto.ImageDto;
import com.volzhin.auction.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{lotId}")
    public List<ImageDto> getImagesByLot(@PathVariable long lotId) {
        return imageService.getImageUrls(lotId);
    }
}
