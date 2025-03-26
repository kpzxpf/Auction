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
import com.volzhin.auction.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
        if (!existsLotById(lotDto.getId())) {
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
    public boolean existsLotById(Long id) {
        return lotRepository.existsById(id);
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
    public List<Lot> findLotsEndingWithin(int minutes, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.minusMinutes(minutes);

        return lotRepository.findLotsEndingWithin(now, target, PageRequest.of(0, size));
    }

    @Transactional(readOnly = true)
    public List<Lot> getLots(int page, int size) {
        return lotRepository.findAllLots(PageRequest.of(page, size));
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
                .currentPrice(lotDto.getCurrentPrice())
                .startTime(lotDto.getStartTime())
                .endTime(lotDto.getEndTime())
                .status(Lot.Status.active)
                .category(categoryService.getCategoryByName((lotDto.getCategoryName())))
                .seller(userService.getUserById(lotDto.getSellerId()))
                .build();
    }
}
