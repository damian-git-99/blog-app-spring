package com.blog.app.user.dto;

import com.blog.app.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserInfoResponseDTO toUserInfoResponseDTO(User user);

}
