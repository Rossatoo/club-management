package com.tcc.club_management.reserva.model;

import com.tcc.club_management.associado.model.Associado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Associado associado;

    @ManyToOne
    private Reserva reserva;

    @Column(name = "data_reserva")
    private LocalDate dataReserva;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    private String observacao;

}
