package kornienko.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"kornienko.service", "kornienko.security", "kornienko.handler", "kornienko.config", "kornienko.model"})
public class JUnit4Config {
}
