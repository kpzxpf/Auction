package com.volzhin.auction.repository;

import com.volzhin.auction.entity.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
