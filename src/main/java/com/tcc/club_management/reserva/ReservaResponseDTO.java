package com.tcc.club_management.reserva;

import com.tcc.club_management.espaco.CategoriaEspaco;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data @AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private Long associadoId;
    private Long UsuarioId;
    private Long espacoId;
    private String espacoNome;
    private CategoriaEspaco categoria;
    private LocalDate dataReserva;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusReserva status;
    private String oberservacao;
}
