package com.tcc.club_management.auth;

import com.tcc.club_management.usuario.Usuario;
import com.tcc.club_management.usuario.UsuarioDTO;
import com.tcc.club_management.usuario.UsuarioService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController (AuthenticationManager authManager, JwtService jwtService,  UsuarioService usuarioService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request){
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );
        String token = jwtService.gerarToken(authentication);
        return new LoginResponseDTO(token, "Bearer");
    }

    @PostMapping("/register")
    public UsuarioDTO register(@RequestBody RegisterRequestDTO req){
        Usuario novo = new  Usuario();
        novo.setNome(req.getNome());
        novo.setEmail(req.getEmail());
        novo.setSenha(req.getSenha());
        novo.setCpf(req.getCpf());
        novo.setPerfil(req.getPerfil() != null ? req.getPerfil() : com.tcc.club_management.usuario.Perfil.COMUM);
        novo.setAtivo(req.getAtivo() != null ? req.getAtivo() : true);

        Usuario salvo = usuarioService.salvar(novo);
        return UsuarioService.toDTO(salvo);
    }
}
