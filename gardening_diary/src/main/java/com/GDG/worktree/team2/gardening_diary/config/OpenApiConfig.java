package com.GDG.worktree.team2.gardening_diary.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) 설정
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Gardening Diary API",
        version = "1.0.0",
        description = "정원 다이어리 API 문서"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Firebase 인증 토큰을 입력하세요 (예: Bearer eyJhbGciOiJSUzI1NiIs...)"
)
public class OpenApiConfig {
}



