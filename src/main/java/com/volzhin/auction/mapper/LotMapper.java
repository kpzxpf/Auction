package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotMapper {
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "category.name", target = "categoryName")
    LotDto toDto(Lot lot);

    LotDto toDto(LotCache lot);

    List<LotDto> toDto(List<Lot> lots);

    List<LotDto> toDtos(List<LotCache> lots);
}
