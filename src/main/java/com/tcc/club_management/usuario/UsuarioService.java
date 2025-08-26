package com.tcc.club_management.usuario;

import com.tcc.club_management.usuario.Usuario;
import com.tcc.club_management.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public UsuarioService(UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos(){
        return usuarioRepository.findAll();
    }

    public Usuario salvar(Usuario usuario){
        //valida duplicidade
        usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email ja cadastrado");
        });

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if(usuario.getAtivo() == null) usuario.setAtivo(true);
        if(usuario.getPerfil() == null) usuario.setPerfil(Perfil.COMUM);

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario atualizar(Long id, Usuario dados){
        return usuarioRepository.findById(id).map(u -> {
            u.setNome(dados.getNome());
            u.setEmail(dados.getEmail());
            u.setCpf(dados.getCpf());
            u.setPerfil(dados.getPerfil());
            u.setAtivo(dados.getAtivo());

            if(dados.getSenha() != null && !dados.getSenha().isBlank()){
                u.setSenha(passwordEncoder.encode(dados.getSenha()));
            }
            return usuarioRepository.save(u);
        }).orElse(null);
    }

    public void deletar(Long id){
        usuarioRepository.deleteById(id);
    }

    public static UsuarioDTO toDTO(Usuario u){
        return new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.getCpf(), u.getPerfil(), u.getAtivo());
    }


}
