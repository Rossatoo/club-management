package com.tcc.club_management.reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByDataReserva(LocalDate data);
    List<Reserva> findByEspaco_Id(Long espacoId);
    List<Reserva> findByAssociado_Id(Long associadoId);
    List<Reserva> findByUsuario_Id(Long usuarioId);

    @Query("""
  select (count(r) > 0) from Reserva r
  where r.espaco.id = :espacoId
    and r.dataReserva = :data
    and r.status <> com.tcc.club_management.reserva.StatusReserva.CANCELADA
    and (:inicio < r.horaFim and :fim > r.horaInicio)
""")
    boolean existsOverlap(@Param("espacoId") Long espacoId,
                          @Param("data") LocalDate data,
                          @Param("inicio") LocalTime inicio,
                          @Param("fim") LocalTime fim);

}
