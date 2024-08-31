package com.cpayusin.file.controller.port;

import com.cpayusin.file.controller.response.FileResponse;
import com.cpayusin.file.domain.File;
import com.cpayusin.member.domain.Member;
import com.cpayusin.post.domain.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{

    FileResponse save(MultipartFile file);

}
