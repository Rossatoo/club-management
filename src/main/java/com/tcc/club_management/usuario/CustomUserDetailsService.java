package com.tcc.club_management.usuario;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Mapear Perfil -> ROLE_<PERFIL>
        String role = "ROLE_" + u.getPerfil().name(); // ADMIN -> ROLE_ADMI

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getSenha()) // já está BCrypt (como você mostrou)
                .roles(u.getPerfil().name()) // equivale a ROLE_<perfil>
                .accountLocked(Boolean.FALSE.equals(u.getAtivo()))
                .disabled(Boolean.FALSE.equals(u.getAtivo()))
                .build();
    }
}

