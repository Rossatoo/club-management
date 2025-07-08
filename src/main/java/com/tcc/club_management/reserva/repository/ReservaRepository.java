package com.tcc.club_management.reserva.repository;

import com.tcc.club_management.reserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByData(LocalDate data);
    List<Reserva> findByEspacoId(Long espacoId);
    List<Reserva> findByAssociadoId(Long associadoId);
}
