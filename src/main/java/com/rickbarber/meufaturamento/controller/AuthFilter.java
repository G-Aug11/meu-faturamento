package com.rickbarber.meufaturamento.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Libera: login, cadastro, logout, arquivos estáticos (HTML/CSS/JS)
        if (path.equals("/api/login") || path.equals("/api/cadastro") || path.equals("/api/logout")
                || !path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;

        if (!LoginController.tokenValido(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\":\"Não autorizado. Faça login.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
