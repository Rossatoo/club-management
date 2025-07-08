package com.tcc.club_management.espaco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "espacos")
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private int capacidade;

    private BigDecimal  valor;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String recursos; //Ex: Wifi, Ar-condicionado, estacionamento

    private Boolean disponivel;

}
