package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.repository.LotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Transactional
    public Lot createLot(LotDto lotDto) {
        return lotRepository.save(lotDtoToLot(lotDto));
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

    public Page<Lot> getLots(int page, int size) {
        return lotRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
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
                .category(categoryService.getCategoryById((lotDto.getCategory_id())))
                .seller(userService.getUserById(lotDto.getSeller_id()))
                .build();
    }
}
