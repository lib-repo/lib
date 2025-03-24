package org.example.libdev.global.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import org.example.libdev.global.ImpApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class IamportConfig {

    private final ImpApiProperties impApiProperties;

    @Bean
    public IamportClient iamportClient(){
        return new IamportClient(impApiProperties.getKey(),impApiProperties.getSecretkey() );

    }
}
