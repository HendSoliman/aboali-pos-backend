// src/main/java/com/aboali/config/OpenApiConfig.java
package com.aboali.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI aboaliOpenAPI() {
        return new OpenAPI()
                
                // ── API Info ─────────────────────────────────────────
                .info(new Info()
                        .title("Arabic POS — REST API")
                        .description("""
                    نظام نقطة البيع العربي — واجهة برمجية كاملة
                    
                    ## Available Modules
                    - **Products** — CRUD + barcode lookup + soft delete
                    - **Orders**   — Create orders, auto stock deduction
                    - **Reports**  — Sales stats, daily chart data
                    - **Settings** — Key-value store for store configuration
                    """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Arabic POS Team")
                                .email("support@aboali.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                
                // ── Servers ──────────────────────────────────────────
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.aboali.com")
                                .description("Production Server")
                ))
                
                // ── Tags ─────────────────────────────────────────────
                .tags(List.of(
                        new Tag().name("Products")
                                 .description("إدارة المنتجات — Product Management"),
                        new Tag().name("Orders")
                                 .description("إدارة الطلبات — Order Management"),
                        new Tag().name("Reports")
                                 .description("التقارير والإحصاءات — Reports & Analytics"),
                        new Tag().name("Settings")
                                 .description("إعدادات المتجر — Store Settings")
                ));
    }
}
