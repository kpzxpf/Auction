package com.volzhin.auction.repository;

import com.volzhin.auction.entity.Lot;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "12"))
    @Query("SELECT l FROM Lot l")
    Slice<Lot> findAllLots(Pageable pageable);
}
