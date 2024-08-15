package com.cpayusin.mapper;

import com.cpayusin.file.infrastructure.FileEntity;
import com.cpayusin.file.controller.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMapper
{
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileResponse toFileResponse(FileEntity file);

    List<FileResponse> toFileResponseList(List<FileEntity> fileList);
}
