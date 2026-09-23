package com.rickbarber.meufaturamento.controller;

import com.rickbarber.meufaturamento.model.Servico;
import com.rickbarber.meufaturamento.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    @Autowired
    private ServicoRepository repo;

    @GetMapping
    public List<Servico> listar() {
        return repo.findByAtivoTrueOrderByIdAsc();
    }

    // Usado pelo agendamento-service pra pegar nome e preço do serviço
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repo.findById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        String nome = (String) body.get("nome");
        if (nome == null || nome.isBlank())
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome obrigatório."));
        Object precoObj = body.get("preco");
        if (precoObj == null)
            return ResponseEntity.badRequest().body(Map.of("erro", "Preço obrigatório."));
        Servico sv = new Servico();
        sv.setNome(nome.trim());
        sv.setIcone(body.getOrDefault("icone", "✂️").toString());
        sv.setPreco(new BigDecimal(precoObj.toString()));
        sv.setAtivo(true);
        Servico salvo = repo.save(sv);
        return ResponseEntity.ok(Map.of("mensagem", "Serviço criado!", "id", salvo.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return repo.findById(id).map(sv -> {
            if (body.containsKey("nome"))  sv.setNome((String) body.get("nome"));
            if (body.containsKey("icone")) sv.setIcone((String) body.get("icone"));
            if (body.containsKey("preco")) sv.setPreco(new BigDecimal(body.get("preco").toString()));
            repo.save(sv);
            return ResponseEntity.ok(Map.of("mensagem", "Serviço atualizado!"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return repo.findById(id).map(sv -> {
            sv.setAtivo(false);
            repo.save(sv);
            return ResponseEntity.ok(Map.of("mensagem", "Serviço removido."));
        }).orElse(ResponseEntity.notFound().build());
    }
}
