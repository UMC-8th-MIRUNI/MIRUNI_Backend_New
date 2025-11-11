package com.miruni.backend.global.annotation;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestBody
@Inherited
public @interface SwaggerBody {

    @AliasFor(annotation = RequestBody.class)
    String description() default "";

    @AliasFor(annotation = RequestBody.class)
    Content[] content() default {};

    @AliasFor(annotation = RequestBody.class)
    boolean required() default false;

    @AliasFor(annotation = RequestBody.class)
    Extension[] extensions() default {};

    @AliasFor(annotation = RequestBody.class)
    String ref() default "";

    @AliasFor(annotation = RequestBody.class)
    boolean useParameterTypeSchema() default false;

}
