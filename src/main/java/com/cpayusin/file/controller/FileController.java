package com.cpayusin.file.controller;

import com.cpayusin.common.controller.response.GlobalResponse;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.file.controller.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@RestController
public class FileController
{
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<GlobalResponse<FileResponse>> save(@RequestPart(value = "file") MultipartFile file)
    {
        FileResponse response = fileService.save(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GlobalResponse<>(response));
    }
}
