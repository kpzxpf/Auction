package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.dto.event.DeleteLotEvent;
import com.volzhin.auction.entity.Image;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.producer.DeleteLotProducer;
import com.volzhin.auction.producer.UpdateLotProducer;
import com.volzhin.auction.repository.LotRepository;
import com.volzhin.auction.service.image.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ImageService imageService;
    private final UpdateLotProducer updateLotProducer;
    private final DeleteLotProducer deleteLotProducer;

    @Transactional
    public Lot createLot(LotDto lotDto, List<MultipartFile> files) {
        lotDto.setStatus(Lot.Status.active);
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
        if (!existsById(lotDto.getId())) {
            log.error("Lot with id {} does not exist", lotDto.getId());
            throw new EntityNotFoundException(String.format("Lot with id %s not found", lotDto.getId()));
        }
        Lot lot = lotRepository.save(lotDtoToLot(lotDto));

        if (lot.getStatus() == Lot.Status.active) {
            updateCacheLot(lot);
        } else {
            deleteLotFromCache(lot.getId());
        }

        return lot;
    }

    @Transactional(readOnly = true)
    public Lot findById(long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lot with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Lot> findLotsEndingWithin(int minutes, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.minusMinutes(minutes);

        return lotRepository.findLotsEndingWithin(now, target, PageRequest.of(0, size));
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return lotRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<Lot> getLots(int page, int size, String categoryName) {
        Pageable pageable = PageRequest.of(page, size);

        if (categoryName != null) {
            return lotRepository.findByCategoryName(categoryName, pageable);
        } else {
            return lotRepository.findAll(pageable).getContent();
        }
    }

    @Transactional(readOnly = true)
    public List<Lot> getLotsByUserId(long userId) {
        return lotRepository.findLotsBySellerId(userId);
    }

    @Transactional
    public void deleteLot(long id) {
        lotRepository.deleteById(id);
        deleteLotFromCache(id);
    }

    @Transactional
    public void save(Lot lot) {
        lotRepository.save(lot);
    }

    public void finishLotById(long lotId) {

    }

    private void updateCacheLot(Lot lot) {
        CompletableFuture.runAsync(() -> updateLotProducer.send(
                LotCache.builder()
                        .id(lot.getId())
                        .title(lot.getTitle())
                        .description(lot.getDescription())
                        .endTime(lot.getEndTime())
                        .urlImages(imageService.getImageUrlsByLotId(lot.getId()))
                        .build()));
    }

    private void deleteLotFromCache(long id) {
        CompletableFuture.runAsync(() -> deleteLotProducer.send(new DeleteLotEvent(id)));
    }

    private Lot lotDtoToLot(LotDto lotDto) {
        return Lot.builder()
                .id(lotDto.getId())
                .title(lotDto.getTitle())
                .description(lotDto.getDescription())
                .startingPrice(lotDto.getStartingPrice())
                .currentPrice(lotRepository.findCurrentPriceById(lotDto.getId()))
                .startTime(lotDto.getStartTime())
                .endTime(lotDto.getEndTime())
                .status(lotDto.getStatus())
                .category(categoryService.getCategoryByName((lotDto.getCategoryName())))
                .seller(userService.getUserById(lotDto.getSellerId()))
                .build();
    }
}
