package com.sparta.finalpj.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(true)
                .consumes(getConsumeContentTypes())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sparta.finalpj"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                ;
    }
    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("image/jpeg;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }
    //.apis(): API 문서를 만들어줄 범위를 지정한다.
    // 만약 apis(RequestHandlerSelectors.basePackage("com.example.demo"))에서는 com.example.demo 하위 구조를 탐색하여 문서를 생성해준다.
    //.paths(): API 의 URL 경로를 지정할 수 있다. .paths(PathSelectors.ant("api/v1/**")) 와 같이 하면 http://localhost/api/v1/ 하위 경로를 가지는 API에 대해 문서를 생성해준다.
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("스프링 부트 API")
                .description("스프링 부트 API Swagger 페이지")
                .version("1.0")
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}

