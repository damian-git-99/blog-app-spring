package com.blog.app.post.dto;

import com.blog.app.post.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostResponseDTO  toPostResponseDTO(Post post);

}
