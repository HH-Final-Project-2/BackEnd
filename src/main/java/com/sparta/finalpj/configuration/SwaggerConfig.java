package com.sparta.finalpj.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any()) // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
                .paths(PathSelectors.ant("/**")) // 그중 /api/** 인 URL들만 필터링
                .build();
    }
    //.apis(): API 문서를 만들어줄 범위를 지정한다.
    // 만약 apis(RequestHandlerSelectors.basePackage("com.example.demo"))에서는 com.example.demo 하위 구조를 탐색하여 문서를 생성해준다.
    //.paths(): API 의 URL 경로를 지정할 수 있다. .paths(PathSelectors.ant("api/v1/**")) 와 같이 하면 http://localhost/api/v1/ 하위 경로를 가지는 API에 대해 문서를 생성해준다.
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("제목 작성")
                .version("버전 작성")
                .description("설명 작성")
                .license("라이센스 작성")
                .licenseUrl("라이센스 URL 작성")
                .build();
    }
}
