package com.volzhin.auction.service;

import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.repository.LotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;

    public Lot findById(long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lot with id {} not found", id);
                   return new EntityNotFoundException(String.format("Lot with id %s not found", id));
                });
    }

    public Lot save(Lot lot) {
        return lotRepository.save(lot);
    }
}
