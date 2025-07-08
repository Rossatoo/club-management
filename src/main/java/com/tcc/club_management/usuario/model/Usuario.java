package com.tcc.club_management.usuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String cpf;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    private Boolean ativo;

}
