package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.PageType;
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

    //Todo HttpServletRequest 추가하기
    @PostMapping(value = "/{page}/scan/cards", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> orcTest(@PathVariable("page") PageType page, @RequestPart(value = "cardImg", required = false) MultipartFile cardImg, HttpServletRequest request) throws IOException {

        return ocrService.readFileInfo(cardImg, request, page);
    }
}
