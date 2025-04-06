package com.volzhin.auction.repository;

import com.volzhin.auction.entity.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> getBidByLotId(long lotId);

    @Query("SELECT b FROM Bid b " +
            "WHERE b.user.id = :userId " +
            "  AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.lot = b.lot)")
    List<Bid> findUserMaxBidsLot(@Param("userId") Long userId);
}
