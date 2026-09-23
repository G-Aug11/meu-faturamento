package com.rickbarber.agendamento.controller;

import com.rickbarber.agendamento.client.FaturamentoClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Esse serviço não tem login próprio: quem sabe se o token é válido é o meu-faturamento.
 * Então a cada requisição a gente pergunta pra ele.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private FaturamentoClient faturamento;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // OPTIONS é a "pergunta" que o navegador faz antes (CORS), não traz token
        if ("OPTIONS".equals(request.getMethod()) || !request.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (!faturamento.tokenValido(request.getHeader("Authorization"))) {
                erro(response, 401, "Não autorizado. Faça login.");
                return;
            }
        } catch (RestClientException e) {
            erro(response, 503, "Sistema principal (meu-faturamento) fora do ar.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void erro(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        // sem esse header o navegador esconde o erro atrás de um erro de CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"erro\":\"" + msg + "\"}");
    }
}
