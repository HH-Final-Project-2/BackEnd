package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.OpenApiRequestDto;
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

    // 자사&타사 명함 등록
    @PostMapping(value = "/companySearch")
    public ResponseDto<?> getpublicInstitutionsApi(@RequestBody OpenApiRequestDto openApiRequestDto) throws IOException, ParseException {
        return openApiService.apiTest(openApiRequestDto);
    }
}
