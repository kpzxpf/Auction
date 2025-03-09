package com.volzhin.auction.service.image;

import com.volzhin.auction.entity.Image;
import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.exception.FileException;
import com.volzhin.auction.repository.ImageRepository;
import com.volzhin.auction.repository.LotRepository;
import com.volzhin.auction.service.LotService;
import com.volzhin.auction.service.image.resize.ResizeService;
import com.volzhin.auction.service.image.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final ResizeService resizeService;
    private final LotService lotService;


    @Transactional
    public Image addImage(Long lotId, MultipartFile imageFile) {
        Lot lot = lotService.findById(lotId);

        MultipartFile suitableImage = resizeFile(imageFile);
        Image image = s3Service.uploadFile(
                suitableImage, createFolder(lotId, imageFile.getContentType()));

        image.setLot(lot);
        image = imageRepository.save(image);
        lot.getImages().add(image);
        lotService.saveLot(lot);

        return image;
    }

    @Transactional
    public List<Image> addImages(Long lotId, List<MultipartFile> imageFiles) {
        Lot lot = lotService.findById(lotId);

        List<MultipartFile> suitableImages = resizeFiles(imageFiles);
        List<Image> images = s3Service.uploadFiles(
                suitableImages, createFolder(lotId, imageFiles.stream().findAny().get().getContentType()));

        images.forEach(image -> {
            image.setLot(lot);
            imageRepository.save(image);
        });

        lot.getImages().addAll(images);
        lotService.saveLot(lot);

        return images;
    }

    @Transactional
    public Image deleteResource(Long imageId) {
        Image resource = getImageById(imageId);

        s3Service.deleteFile(resource.getKey());
        imageRepository.deleteById(imageId);

        return resource;
    }

    private MultipartFile resizeFile(MultipartFile file) {
        try {
            return resizeService.resizeImage(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to resize image: " + file.getOriginalFilename());
        }
    }

    private List<MultipartFile> resizeFiles(List<MultipartFile> files) {
        List<MultipartFile> resizedFiles = new ArrayList<>();

        files.forEach(file -> {
            try {
                resizedFiles.add(resizeService.resizeImage(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new FileException("Failed to resize image: " + file.getOriginalFilename());
            }
        });
        return resizedFiles;
    }

    private Image getImageById(Long resourceId) {
        return imageRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error(String.format("Resource id: %d not found", resourceId));
                    return new EntityNotFoundException(String.format("Resource id: %d not found", resourceId));
                });
    }

    private String createFolder(Long postId, String fileType) {
        return String.format("Post%s%s", postId, fileType.replaceAll("/.*$", ""));
    }
}
