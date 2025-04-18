package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.BidDto;
import com.volzhin.auction.entity.bid.Bid;
import com.volzhin.auction.entity.bid.BidCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BidMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "lot.id", target = "lotId")
    @Mapping(source = "createdAt", target = "bidTime")
    BidDto toDto(Bid bid);

    BidCache toCache(BidDto bidDto);

    List<BidDto> toDto(List<Bid> bids);
}
