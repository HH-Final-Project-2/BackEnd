package com.sparta.finalpj.controller;

import com.sparta.finalpj.service.GoogleCloudUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor @RestController @RequestMapping(value = "/api") public class OcrController {
    private final GoogleCloudUploadService googleCloudUploadService;
    @PostMapping(value = "/{path}/upload/img", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String scanCard(@PathVariable("path") String path,
                           @RequestPart(value = "cardImg", required = false)
                           MultipartFile cardImg, HttpServletRequest request) throws IOException {
    return googleCloudUploadService.upload(path, cardImg, request);
    }
}
