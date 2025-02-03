package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Lot;
import com.volzhin.auction.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotMapper {

    @Mapping(source = "seller_id", target = "seller", qualifiedByName = "idToUser")
    Lot toEntity(LotDto lotDto);

    @Mapping(source = "seller.id", target = "seller_id")
    LotDto toDto(Lot lot);

    List<LotDto> toDto(List<Lot> lots);

    List<Lot> toEntity(List<LotDto> lotDtos);

    @Named("idToUser")
    default User idToUser(long sellerId) {
        if (sellerId <= 0) {
            return null;
        }
        User user = new User();
        user.setId(sellerId);
        return user;
    }
}
