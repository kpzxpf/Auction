package com.volzhin.auction.controller;

import com.volzhin.auction.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    /*@GetMapping("/{lotId}")
    public MultipartFile getImageByLot(@PathVariable long lotId) {

    }

    @GetMapping("/{lotId}/list")
    public List<MultipartFile> getImagesByLot(@PathVariable long lotId) {

    }*/


    @PostMapping(value = "/{lotId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveImage(@PathVariable long lotId, @RequestParam("file") MultipartFile file) {
        imageService.addImage(lotId, file);
    }

    @PostMapping(value = "/{lotId}/list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveImages(@PathVariable long lotId, @RequestParam("files") List<MultipartFile> files) {
        imageService.addImages(lotId, files);
    }

    @DeleteMapping("/{lotId}")
    public void deleteImage(@PathVariable long lotId) {}
}