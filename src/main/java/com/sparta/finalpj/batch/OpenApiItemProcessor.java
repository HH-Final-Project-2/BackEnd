package com.sparta.finalpj.batch;

import com.sparta.finalpj.domain.CompanyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class OpenApiItemProcessor implements ItemProcessor<List<PublicOpenApiResponseDto>, List<CompanyInfo>> {

    @Override
    public List<CompanyInfo> process(List<PublicOpenApiResponseDto> openApiResponseDtoList) {
        List<CompanyInfo> companyInfoList = new ArrayList<>();
        for (PublicOpenApiResponseDto publicOpenApiResponseDto : openApiResponseDtoList) {
            companyInfoList.add(
                    CompanyInfo.builder()
                            .crno(publicOpenApiResponseDto.getCrno())
                            .companyName(publicOpenApiResponseDto.getCompanyName())
                            .companyAddress(publicOpenApiResponseDto.getCompanyAddress())
                            .build()
            );
        }
        return companyInfoList;
    }
}
