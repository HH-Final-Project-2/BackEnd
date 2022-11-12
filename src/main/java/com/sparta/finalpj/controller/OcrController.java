package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class OcrController {
    private final OcrService ocrService;
//    private final S3Service s3Service;

    //Todo HttpServletRequest 추가하기
    @PostMapping(value = "/member/ocrtest")
    public ResponseDto<?> orcTest(@RequestPart(value = "cardImg", required = false) MultipartFile cardImg) throws IOException {

//       return s3Service.upload(cardImg);
       return null;
    }
}
