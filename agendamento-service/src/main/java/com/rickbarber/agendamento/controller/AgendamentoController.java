package com.rickbarber.agendamento.controller;

import com.rickbarber.agendamento.client.FaturamentoClient;
import com.rickbarber.agendamento.model.Agendamento;
import com.rickbarber.agendamento.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private static final List<String> STATUS_VALIDOS = List.of("AGENDADO", "CONCLUIDO", "CANCELADO");

    @Autowired private AgendamentoRepository repo;
    @Autowired private FaturamentoClient faturamento;

    // GET /api/agendamentos?data=2026-09-22  (sem data = hoje)
    @GetMapping
    public List<Agendamento> listar(@RequestParam(defaultValue = "") String data) {
        LocalDate dia = data.isBlank() ? LocalDate.now() : LocalDate.parse(data);
        return repo.findByDataHoraBetweenOrderByDataHoraAsc(dia.atStartOfDay(), dia.atTime(LocalTime.MAX));
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body,
                                   @RequestHeader("Authorization") String auth) {
        // 1. Data e hora
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(String.valueOf(body.get("dataHora")));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Data/hora inválida."));
        }
        if (dataHora.isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body(Map.of("erro", "Não dá pra agendar no passado."));
        if (repo.existsByDataHoraAndStatus(dataHora, "AGENDADO"))
            return ResponseEntity.badRequest().body(Map.of("erro", "Esse horário já está ocupado."));

        Agendamento a = new Agendamento();
        a.setDataHora(dataHora);

        // 2. Serviço (obrigatório) -> pergunta pro meu-faturamento
        Object svId = body.get("servicoId");
        if (svId == null || svId.toString().isBlank())
            return ResponseEntity.badRequest().body(Map.of("erro", "Selecione um serviço."));
        Map<String, Object> servico = faturamento.buscarServico(Long.parseLong(svId.toString()), auth);
        if (servico == null)
            return ResponseEntity.badRequest().body(Map.of("erro", "Serviço não encontrado."));
        a.setServicoId(Long.parseLong(svId.toString()));
        a.setNomeServico((String) servico.get("nome"));

        // 3. Cliente cadastrado OU nome avulso
        Object cliId = body.get("clienteId");
        if (cliId != null && !cliId.toString().isBlank()) {
            Map<String, Object> cliente = faturamento.buscarCliente(Long.parseLong(cliId.toString()), auth);
            if (cliente == null)
                return ResponseEntity.badRequest().body(Map.of("erro", "Cliente não encontrado."));
            a.setClienteId(Long.parseLong(cliId.toString()));
            a.setNomeCliente((String) cliente.get("nome"));
        } else {
            String nomeAvulso = (String) body.get("nomeAvulso");
            if (nomeAvulso == null || nomeAvulso.isBlank())
                return ResponseEntity.badRequest().body(Map.of("erro", "Informe o cliente."));
            a.setNomeCliente(nomeAvulso.trim());
        }

        a.setObservacao((String) body.getOrDefault("observacao", ""));
        Agendamento salvo = repo.save(a);
        return ResponseEntity.ok(Map.of("mensagem", "Agendamento criado!", "id", salvo.getId()));
    }

    // PUT /api/agendamentos/5/status   body: {"status":"CONCLUIDO"}
    @PutMapping("/{id}/status")
    public ResponseEntity<?> mudarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || !STATUS_VALIDOS.contains(status))
            return ResponseEntity.badRequest().body(Map.of("erro", "Status inválido."));
        return repo.findById(id).<ResponseEntity<?>>map(a -> {
            a.setStatus(status);
            repo.save(a);
            return ResponseEntity.ok(Map.of("mensagem", "Agendamento atualizado!"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("mensagem", "Agendamento removido."));
    }
}
