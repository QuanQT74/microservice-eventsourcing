package com.ltfullstack.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.naming.Name;

@OpenAPIDefinition(
        info = @Info(
                title = "Employee Api Specification - LT FullStack",
                description = "Api doccumentation for Employee Service",
                version = "1.0",
                contact = @Contact(
                        name = "Phan Nguyễn Đông Quân",
                        email = "quankietsuat@gmail.com"
                ),
                license = @License(
                        name = "MIT Lcense"
                )
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:9001/"
                ),
                @Server(
                        description = "Dev ENV",
                        url = "https://employee-service.dev.com"
                ),
                @Server(
                        description = "Prob ENV",
                        url = "https://employee-service.prob.com"
                )
        }
)
public class OpenApiConfig {
}
