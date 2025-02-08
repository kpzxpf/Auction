package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotMapper {
    @Mapping(source = "seller.id", target = "seller_id")
    @Mapping(source = "category.id", target = "category_id")
    LotDto toDto(Lot lot);

    List<LotDto> toDto(List<Lot> lots);

    default Page<LotDto> toDto(Page<Lot> lots) {
        List<LotDto> lotDtos = lots.getContent().stream()
                .map(this::toDto)
                .toList();

        return new PageImpl<>(lotDtos, lots.getPageable(), lots.getTotalElements());
    }
}
