package com.rickbarber.agendamento.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Toda comunicação com o meu-faturamento passa por aqui, via HTTP.
 * O agendamento-service nunca acessa o banco do meu-faturamento direto.
 */
@Component
public class FaturamentoClient {

    @Value("${faturamento.url}")
    private String baseUrl;

    private final RestTemplate rest = new RestTemplate();

    // Pergunta pro meu-faturamento se o token é válido.
    // Se ele estiver fora do ar, o RestTemplate lança RestClientException (tratado no AuthFilter).
    public boolean tokenValido(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        try {
            rest.exchange(baseUrl + "/api/sessao", HttpMethod.GET, comToken(authHeader), Map.class);
            return true;
        } catch (HttpClientErrorException e) {
            return false; // 401 ou 403
        }
    }

    public Map<String, Object> buscarCliente(Long id, String authHeader) {
        return buscar("/api/clientes/" + id, authHeader);
    }

    public Map<String, Object> buscarServico(Long id, String authHeader) {
        return buscar("/api/servicos/" + id, authHeader);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buscar(String caminho, String authHeader) {
        try {
            return rest.exchange(baseUrl + caminho, HttpMethod.GET, comToken(authHeader), Map.class).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    // Repassa o mesmo token que o front mandou pra gente
    private HttpEntity<Void> comToken(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return new HttpEntity<>(headers);
    }
}
