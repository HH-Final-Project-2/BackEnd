package com.sparta.finalpj.batch;

import com.sparta.finalpj.domain.CompanyInfo;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DeleteDataTasklet deleteDataTasklet;
    private final PublicOpenApiService publicOpenApiService;

    @Bean
    public Job openApiJob() throws Exception {
        return jobBuilderFactory.get("openApiJob")
                .start(deleteDataStep())
                .next(openApiStep())
                .build();
    }

    @Bean
    public Step deleteDataStep() {
        return stepBuilderFactory.get("deleteDataStep")
                .tasklet(deleteDataTasklet)
                .build();
    }

    @Bean
    public Step openApiStep() throws Exception {
        return stepBuilderFactory.get("openApiStep")
                .<List<PublicOpenApiResponseDto>, List<CompanyInfo>>chunk(1)
                .reader(openApiItemReader())
                .processor(openApiItemProcessor())
                .writer(openApiItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<List<PublicOpenApiResponseDto>> openApiItemReader() throws Exception {
        return new OpenApiItemReader();
    }
    @Bean
    public ItemProcessor<List<PublicOpenApiResponseDto>, List<CompanyInfo>> openApiItemProcessor() {
        return new OpenApiItemProcessor();
    }

    @Bean
    public ItemWriter<List<CompanyInfo>> openApiItemWriter() {
        return new OpenApiItemWriter();
    }

}
