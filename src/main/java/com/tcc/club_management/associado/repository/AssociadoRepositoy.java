package com.tcc.club_management.associado.repository;

import com.tcc.club_management.associado.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
public interface AssociadoRepositoy extends JpaRepository<Associado, Long> {

    List<Associado> findByTitularId(Long id);

}
