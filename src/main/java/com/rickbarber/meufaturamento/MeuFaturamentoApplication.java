package com.rickbarber.meufaturamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeuFaturamentoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeuFaturamentoApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  MEU FATURAMENTO - Rick Barber");
        System.out.println("  Sistema rodando em: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
