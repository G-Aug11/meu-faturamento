package com.rickbarber.meufaturamento.controller;

import com.rickbarber.meufaturamento.model.Cliente;
import com.rickbarber.meufaturamento.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository repo;

    @GetMapping
    public List<Cliente> listar(@RequestParam(required = false) String busca) {
        if (busca != null && !busca.isBlank())
            return repo.findByNomeContainingIgnoreCaseOrTelefoneContaining(busca, busca);
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        if (nome == null || nome.isBlank())
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome obrigatório."));
        Cliente c = new Cliente();
        c.setNome(nome.trim());
        c.setTelefone((String) body.getOrDefault("telefone", ""));
        c.setObservacao((String) body.getOrDefault("observacao", ""));
        c.setCriadoEm(LocalDateTime.now());
        Cliente salvo = repo.save(c);
        return ResponseEntity.ok(Map.of("mensagem", "Cliente cadastrado!", "id", salvo.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return repo.findById(id).map(c -> {
            if (body.containsKey("nome"))       c.setNome((String) body.get("nome"));
            if (body.containsKey("telefone"))   c.setTelefone((String) body.get("telefone"));
            if (body.containsKey("observacao")) c.setObservacao((String) body.get("observacao"));
            repo.save(c);
            return ResponseEntity.ok(Map.of("mensagem", "Cliente atualizado!"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("mensagem", "Cliente removido."));
    }
}
