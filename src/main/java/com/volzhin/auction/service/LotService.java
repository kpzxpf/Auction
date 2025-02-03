package com.volzhin.auction.service;

import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.repository.LotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;

    @Transactional
    public Lot createLot(Lot lot) {
        return lotRepository.save(lot);
    }

    @Transactional
    public Lot updateLot(Lot lot) {
        return lotRepository.save(lot);
    }

    @Transactional(readOnly = true)
    public Lot findById(long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lot with id {} not found", id);
                   return new EntityNotFoundException(String.format("Lot with id %s not found", id));
                });
    }

    @Transactional
    public Lot saveLot(Lot lot) {
        return lotRepository.save(lot);
    }
}
