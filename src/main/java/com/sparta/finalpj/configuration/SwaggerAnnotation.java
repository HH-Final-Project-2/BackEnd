package com.sparta.finalpj.configuration;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
        @ApiImplicitParam(
                name = "Refresh_Token",
                value = "Refresh 토큰",
                required = true,
                dataTypeClass = String.class,
                paramType = "header"
        )
})
public @interface SwaggerAnnotation {

}
