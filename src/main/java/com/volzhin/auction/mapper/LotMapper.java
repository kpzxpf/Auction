package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotMapper {
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "category.id", target = "categoryId")
    LotDto toDto(Lot lot);

    List<LotDto> toDto(List<Lot> lots);
}
