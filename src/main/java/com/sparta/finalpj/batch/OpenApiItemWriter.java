package com.sparta.finalpj.batch;

import com.sparta.finalpj.domain.CompanyInfo;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OpenApiItemWriter implements ItemWriter<List<CompanyInfo>> {

    @Autowired
    private PublicOpenApiService publicOpenApiService;

    @Override
    public void write(List<? extends List<CompanyInfo>> list) {
        this.publicOpenApiService.saveAll(list.get(0));
    }
}
