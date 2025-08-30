package com.microservico.vendas.Config.Security.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    public UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado no contexto de segurança.");
        }
        String userIdString = (String) authentication.getPrincipal();
        return UUID.fromString(userIdString);
    }
}
