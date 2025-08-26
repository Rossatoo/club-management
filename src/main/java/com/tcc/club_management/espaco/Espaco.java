package com.tcc.club_management.espaco;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String recursos; //Ex: Wifi, Ar-condicionado, estacionamento

    private Boolean disponivel;

    @Enumerated(EnumType.STRING)
    private CategoriaEspaco categoria;

    @Column(name = "preco_hora_socio")
    private BigDecimal precoHoraSocio;

    @Column(name = "preco_hora_comum")
    private BigDecimal precoHoraComum;

    @Column(name = "preco_dia_socio")
    private BigDecimal precoDiaSocio;

    @Column(name = "preco_dia_comum")
    private BigDecimal precoDiaComum;

}
