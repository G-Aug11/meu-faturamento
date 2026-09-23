package com.rickbarber.meufaturamento.controller;

import com.rickbarber.meufaturamento.model.Usuario;
import com.rickbarber.meufaturamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Compara e gera hashes de senha (nunca armazenamos senha em texto puro)
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Armazena tokens em memória (simples — reinicia ao reiniciar o servidor)
    private static final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String senha   = body.get("senha");

        if (usuario == null || senha == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Usuário e senha obrigatórios."));
        }

        Optional<Usuario> opt = usuarioRepository.findByUsuario(usuario.trim());

        if (opt.isEmpty() || !passwordEncoder.matches(senha, opt.get().getSenha())) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário ou senha incorretos."));
        }

        String token = UUID.randomUUID().toString();
        tokens.put(token, usuario.trim());

        return ResponseEntity.ok(Map.of("token", token, "usuario", usuario.trim()));
    }

    // Cria um usuário novo pela tela inicial e já devolve o token (entra direto)
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String senha   = body.get("senha");

        if (usuario == null || usuario.isBlank() || senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Usuário e senha obrigatórios."));
        }
        usuario = usuario.trim();
        if (usuario.length() < 3 || usuario.length() > 60) {
            return ResponseEntity.badRequest().body(Map.of("erro", "O usuário deve ter entre 3 e 60 caracteres."));
        }
        if (senha.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A senha deve ter pelo menos 6 caracteres."));
        }
        if (usuarioRepository.findByUsuario(usuario).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Esse usuário já existe."));
        }

        Usuario novo = new Usuario();
        novo.setUsuario(usuario);
        novo.setSenha(passwordEncoder.encode(senha)); // salva o hash, nunca a senha pura
        usuarioRepository.save(novo);

        String token = UUID.randomUUID().toString();
        tokens.put(token, usuario);
        return ResponseEntity.ok(Map.of("mensagem", "Conta criada!", "token", token, "usuario", usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            tokens.remove(auth.substring(7));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Outros serviços (ex: agendamento-service) chamam aqui pra saber se o token é válido.
    // Se o token for inválido o AuthFilter já barra antes com 401, então se chegou aqui tá ok.
    @GetMapping("/sessao")
    public ResponseEntity<?> sessao(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(Map.of("usuario", tokens.get(auth.substring(7))));
    }

    // Método estático para validar token (usado pelos outros controllers via filtro)
    public static boolean tokenValido(String token) {
        return token != null && tokens.containsKey(token);
    }
}
