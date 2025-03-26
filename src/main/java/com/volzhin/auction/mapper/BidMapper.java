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
    BidDto toDto(Bid bid);

    Bid toEntity(BidDto bidDto);

    @Mapping(target = "lotId", source = "lotId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "amount", source = "amount")
    BidCache toCache(BidDto bidDto);

    List<BidDto> toDto(List<Bid> bids);

    List<Bid> toEntity(List<BidDto> bidDtos);
}
