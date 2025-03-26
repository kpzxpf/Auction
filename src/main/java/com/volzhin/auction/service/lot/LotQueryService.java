package com.volzhin.auction.service.lot;

import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.repository.LotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotQueryService {
    private final LotRepository lotRepository;

    public Lot findById(long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lot with id " + id + " not found"));
    }

    public List<Lot> findLotsEndingWithin(int minutes, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.minusMinutes(minutes);

        return lotRepository.findLotsEndingWithin(now, target, PageRequest.of(0, size));
    }

    public boolean existsById(long id) {
        return lotRepository.existsById(id);
    }

    public List<Lot> getLots(int page, int size) {
        return lotRepository.findAllLots(PageRequest.of(page, size));
    }

    public List<Lot> getLotsByUserId(long userId) {
        return lotRepository.findLotsBySellerId(userId);
    }
}