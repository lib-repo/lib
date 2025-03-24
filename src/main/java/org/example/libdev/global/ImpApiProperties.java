package org.example.libdev.global;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Configuration
@Setter
@ConfigurationProperties(prefix = "imp.api")
public class ImpApiProperties {
    private String key;
    private String secretkey;
}
