package com.microservico.vendas.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Value("${internal.api.key}")
    private String apiKey;

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-API-KEY", apiKey);
    }
}
