package com.tcc.club_management.auth;


import com.tcc.club_management.usuario.Perfil;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class RegisterRequestDTO {
     @NotNull private String nome;
     @NotNull private String email;
     @NotNull private String senha;
     @NotNull private String cpf;
     private Perfil perfil = Perfil.COMUM;
     private Boolean ativo = true;
}
