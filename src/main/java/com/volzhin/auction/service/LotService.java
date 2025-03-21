package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Image;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.repository.LotRepository;
import com.volzhin.auction.service.image.ImageService;
import com.volzhin.auction.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ImageService imageService;

    @Transactional
    public Lot createLot(LotDto lotDto, List<MultipartFile> files) {
        Lot lot = lotDtoToLot(lotDto);
        lot.setCurrentPrice(lotDto.getStartingPrice());

        lot = lotRepository.save(lot);
        List<Image> images = imageService.addImages(lot, files);
        Lot finalLot = lot;
        images.forEach(image -> image.setLot(finalLot));
        lot.setImages(images);

        return lotRepository.save(lot);
    }

    @Transactional
    public Lot updateLot(LotDto lotDto) {
        return lotRepository.save(lotDtoToLot(lotDto));
    }

    @Transactional(readOnly = true)
    public Lot findById(long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lot with id {} not found", id);
                   return new EntityNotFoundException(String.format("Lot with id %s not found", id));
                });
    }

    @Transactional(readOnly = true)
    public List<Lot> findActiveLotsSortedByEndTime(int cacheSize) {
        return lotRepository.findActiveLotsSortedByEndTime(PageRequest.of(0, cacheSize));
    }

    @Transactional(readOnly = true)
    public List<Lot> getLots(int page, int size) {
        return lotRepository.findAllLots(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<Lot> getLotsByUserId(long userId) {
        return lotRepository.findLotsBySellerId(userId);
    }

    @Transactional(readOnly = true)
    public Lot saveLot(Lot lot) {
        return lotRepository.save(lot);
    }

    private Lot lotDtoToLot(LotDto lotDto) {
        return Lot.builder()
                .id(lotDto.getId())
                .title(lotDto.getTitle())
                .description(lotDto.getDescription())
                .startingPrice(lotDto.getStartingPrice())
                .currentPrice(lotDto.getCurrentPrice())
                .startTime(lotDto.getStartTime())
                .endTime(lotDto.getEndTime())
                .status(Lot.Status.active)
                .category(categoryService.getCategoryByName((lotDto.getCategoryName())))
                .seller(userService.getUserById(lotDto.getSellerId()))
                .build();
    }
}
