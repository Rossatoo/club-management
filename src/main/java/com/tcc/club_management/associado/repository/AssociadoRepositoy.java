package com.tcc.club_management.associado.repository;

import com.tcc.club_management.associado.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssociadoRepositoy extends JpaRepository<Associado, Long> {

    List<Associado> findByTitularId(Long id);

}
