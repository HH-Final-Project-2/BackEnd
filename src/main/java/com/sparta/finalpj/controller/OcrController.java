package com.sparta.finalpj.controller;

import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.GoogleCloudUploadService;
import com.sparta.finalpj.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class OcrController {
    private final OcrService ocrService;

    // 명함 이미지 업로드
    @SwaggerAnnotation
    @PostMapping(value = "/upload/img", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> scanCard(@RequestPart(value = "cardImg", required = false)
                                   MultipartFile cardImg, HttpServletRequest request) throws IOException {
        return ocrService.readFileInfo(cardImg, request);
    }
}
