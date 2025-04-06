package com.volzhin.auction.repository;

import com.volzhin.auction.entity.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> getBidByLotId(long lotId);
}
