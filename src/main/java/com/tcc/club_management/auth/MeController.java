package com.tcc.club_management.auth;

// package com.tcc.club_management.auth;

import com.tcc.club_management.usuario.Usuario;
import com.tcc.club_management.usuario.UsuarioDTO;
import com.tcc.club_management.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class MeController {

    private final UsuarioRepository usuarioRepository;

    public MeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/me")
    public UsuarioDTO me(Authentication auth) {
        // auth.getName() = email do subject do JWT
        Usuario u = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UsuarioDTO(
                u.getId(), u.getNome(), u.getEmail(), u.getCpf(), u.getPerfil(), u.getAtivo()
        );
    }
}

