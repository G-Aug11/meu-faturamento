package com.rickbarber.agendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgendamentoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgendamentoServiceApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  AGENDAMENTO-SERVICE - Rick Barber");
        System.out.println("  Rodando em: http://localhost:8081");
        System.out.println("========================================\n");
    }
}
