package com.tcc.club_management.associado.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tcc.club_management.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "associados")
public class Associado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String telefone;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @OneToOne
    private Usuario usuario;

    @ManyToOne // - Cada dependente pode estar ligado a 1 titular
    @JsonBackReference
    private Associado titular; // - se for 'null', é o titular da associação

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL,  orphanRemoval = true) // - 1 titular pode ter vários dependentes
    @JsonManagedReference // - Evita loop infinito ao converter JSON dos dependentes
    private List<Associado> dependentes;


}
