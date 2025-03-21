package com.volzhin.auction.repository;

import com.volzhin.auction.entity.lot.Lot;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "12"))
    @Query("SELECT l FROM Lot l")
    List<Lot> findAllLots(Pageable pageable);

    List<Lot> findLotsBySellerId(Long userId);

    @Query("SELECT l FROM Lot l WHERE l.status = 'active' ORDER BY l.endTime ASC")
    List<Lot> findActiveLotsSortedByEndTime(Pageable pageable);
}
