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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            tokens.remove(auth.substring(7));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Método estático para validar token (usado pelos outros controllers via filtro)
    public static boolean tokenValido(String token) {
        return token != null && tokens.containsKey(token);
    }
}
