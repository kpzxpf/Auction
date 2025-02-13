package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotMapper {
    @Mapping(source = "seller.id", target = "seller_id")
    @Mapping(source = "category.id", target = "category_id")
    LotDto toDto(Lot lot);

    List<LotDto> toDto(List<Lot> lots);

    default Slice<LotDto> toDto(Slice<Lot> lots) {
        List<LotDto> lotDtos = lots.getContent().stream().map(this::toDto).toList();
        return new SliceImpl<>(lotDtos, lots.getPageable(), lots.hasNext());
    }

}
