package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}
