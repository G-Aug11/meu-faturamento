package com.rickbarber.meufaturamento.controller;

import com.rickbarber.meufaturamento.model.Atendimento;
import com.rickbarber.meufaturamento.model.Servico;
import com.rickbarber.meufaturamento.repository.AtendimentoRepository;
import com.rickbarber.meufaturamento.repository.ClienteRepository;
import com.rickbarber.meufaturamento.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/atendimentos")
@CrossOrigin(origins = "*")
public class AtendimentoController {

    @Autowired private AtendimentoRepository atendimentoRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ServicoRepository servicoRepo;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "") String inicio,
            @RequestParam(defaultValue = "") String fim) {

        LocalDateTime dtInicio = inicio.isBlank()
            ? LocalDate.now().withDayOfMonth(1).atStartOfDay()
            : LocalDate.parse(inicio).atStartOfDay();
        LocalDateTime dtFim = fim.isBlank()
            ? LocalDate.now().atTime(LocalTime.MAX)
            : LocalDate.parse(fim).atTime(LocalTime.MAX);

        List<Atendimento> lista = atendimentoRepo.findByDataHoraBetweenOrderByDataHoraDesc(dtInicio, dtFim);
        BigDecimal total = atendimentoRepo.totalPeriodo(dtInicio, dtFim);
        Long qtd = atendimentoRepo.countPeriodo(dtInicio, dtFim);

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Atendimento a : lista) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id",             a.getId());
            item.put("nomeCliente",    a.getNomeCliente());
            item.put("dataHora",       a.getDataHora() != null ? a.getDataHora().toString() : "");
            item.put("total",          a.getTotal());
            item.put("formaPagamento", a.getFormaPagamento() != null ? a.getFormaPagamento() : "");
            item.put("observacao",     a.getObservacao() != null ? a.getObservacao() : "");

            List<Map<String, Object>> svs = new ArrayList<>();
            Set<Long> vistos = new HashSet<>();
            if (a.getServicos() != null) {
                for (Servico s : a.getServicos()) {
                    if (vistos.add(s.getId())) {
                        Map<String, Object> sv = new LinkedHashMap<>();
                        sv.put("nome",  s.getNome());
                        sv.put("icone", s.getIcone() != null ? s.getIcone() : "✂️");
                        sv.put("preco", s.getPreco());
                        svs.add(sv);
                    }
                }
            }
            item.put("servicos", svs);
            resultado.add(item);
        }

        return ResponseEntity.ok(Map.of(
            "atendimentos",    resultado,
            "totalPeriodo",    total,
            "qtdAtendimentos", qtd
        ));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        Atendimento a = new Atendimento();
        a.setDataHora(LocalDateTime.now());

        Object cliId = body.get("clienteId");
        if (cliId != null && !cliId.toString().equals("null"))
            clienteRepo.findById(Long.parseLong(cliId.toString())).ifPresent(a::setCliente);

        String nomeAvulso = (String) body.get("nomeAvulso");
        if (nomeAvulso != null && !nomeAvulso.isBlank()) a.setNomeAvulso(nomeAvulso.trim());

        a.setFormaPagamento((String) body.getOrDefault("formaPagamento", "dinheiro"));
        a.setObservacao((String) body.getOrDefault("observacao", ""));

        List<?> svIds = (List<?>) body.get("servicoIds");
        if (svIds == null || svIds.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("erro", "Selecione ao menos um serviço."));

        List<Servico> servicos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Set<Long> vistos = new HashSet<>();
        for (Object sid : svIds) {
            Long svId = Long.parseLong(sid.toString());
            if (vistos.add(svId)) {
                Optional<Servico> sv = servicoRepo.findById(svId);
                if (sv.isPresent()) {
                    servicos.add(sv.get());
                    total = total.add(sv.get().getPreco());
                }
            }
        }

        a.setServicos(servicos);
        a.setTotal(total);
        Atendimento salvo = atendimentoRepo.save(a);

        return ResponseEntity.ok(Map.of(
            "mensagem",      "Atendimento registrado!",
            "atendimentoId", salvo.getId(),
            "total",         salvo.getTotal()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!atendimentoRepo.existsById(id)) return ResponseEntity.notFound().build();
        atendimentoRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("mensagem", "Atendimento #" + id + " removido."));
    }
}
