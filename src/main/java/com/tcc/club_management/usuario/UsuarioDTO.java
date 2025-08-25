package com.tcc.club_management.usuario;

// package com.tcc.club_management.usuario.dto;
import com.tcc.club_management.usuario.Perfil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Perfil perfil;
    private Boolean ativo;
}
