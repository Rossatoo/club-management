package com.tcc.club_management.reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByDataReserva(LocalDate data);
    List<Reserva> findByEspaco_Id(Long espacoId);
    List<Reserva> findByAssociado_Id(Long associadoId);
}
