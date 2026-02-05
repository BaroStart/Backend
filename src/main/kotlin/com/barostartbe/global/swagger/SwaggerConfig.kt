package land.leets.global.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "바로잇 API",
        description = "바로잇 API 문서",
        version = "v0.0.1"
    )
)
@Configuration
class SwaggerConfig(
    @Value("\${app.server.url}")
    private val serverUrl: String
) {

    @Bean
    fun openApi(): OpenAPI {
        val jwt = "JWT"
        val securityRequirement = SecurityRequirement().addList("JWT")
        val components = Components().addSecuritySchemes(
            jwt,
            SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        )

        return OpenAPI()
            .addServersItem(Server().url(serverUrl))
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}
