package com.cpayusin.file.mapper;

import com.cpayusin.file.domain.File;
import com.cpayusin.file.controller.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMapper
{
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileResponse toFileResponse(File file);

    List<FileResponse> toFileResponseList(List<File> fileList);
}
