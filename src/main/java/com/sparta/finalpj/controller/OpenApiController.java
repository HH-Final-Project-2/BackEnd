package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.OpenApiRequestDto;
import com.sparta.finalpj.controller.response.OpenApiResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class OpenApiController {
    private final OpenApiService openApiService;

    // 공공데이터 호출 => 기업정보 전체조회
    @PostMapping(value = "/companySearch")
    public ResponseDto<?> getPublicInstitutionsApi(@RequestBody OpenApiRequestDto requestDto) throws IOException, ParseException {
        return openApiService.getPublicInstitutionsApi(requestDto);
    }
    
    // 기업정보 받아오기
    @PostMapping("/companyInfo")
    public ResponseDto<?> getCompanyInfo(@RequestBody OpenApiResponseDto requestDto) {
        return openApiService.getCompanyInfo(requestDto);
    }
}
