package com.rickbarber.meufaturamento.controller;

import com.rickbarber.meufaturamento.repository.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private AtendimentoRepository repo;

    @GetMapping
    public Map<String, Object> dashboard() {
        LocalDateTime hojeI = LocalDate.now().atStartOfDay();
        LocalDateTime hojeF = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime mesI  = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime mesF  = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
        LocalDateTime set7  = LocalDateTime.now().minusDays(7);

        Map<String, Object> hoje = new HashMap<>();
        hoje.put("total", repo.totalPeriodo(hojeI, hojeF));
        hoje.put("qtd",   repo.countPeriodo(hojeI, hojeF));

        Map<String, Object> mes = new HashMap<>();
        mes.put("total", repo.totalPeriodo(mesI, mesF));
        mes.put("qtd",   repo.countPeriodo(mesI, mesF));

        List<Map<String, Object>> top = new ArrayList<>();
        for (Object[] row : repo.topServicos()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nome",  row[0]);
            item.put("icone", row[1] != null ? row[1] : "✂️");
            item.put("qtd",   row[2]);
            item.put("total", row[3]);
            top.add(item);
        }

        List<Map<String, Object>> formas = new ArrayList<>();
        for (Object[] row : repo.formasPagamentoPeriodo(mesI, mesF)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("forma", row[0] != null ? row[0] : "—");
            item.put("qtd",   row[1]);
            item.put("total", row[2]);
            formas.add(item);
        }

        List<Map<String, Object>> dias = new ArrayList<>();
        for (Object[] row : repo.ultimos7Dias(set7)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("dia",   row[0] != null ? row[0].toString() : "");
            item.put("qtd",   row[1]);
            item.put("total", row[2] != null ? row[2] : BigDecimal.ZERO);
            dias.add(item);
        }

        return Map.of(
            "hoje", hoje, "mes", mes,
            "topServicos", top, "formasPag", formas, "ultimos7Dias", dias
        );
    }
}
