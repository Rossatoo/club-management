package com.tcc.club_management.associado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Repository
@CrossOrigin
public interface AssociadoRepository extends JpaRepository<Associado, Long> {

    Optional<Associado> findByUsuario_Email(String email);

    Optional<Associado> findByUsuarioId(Long usuarioId);
    List<Associado> findByTitularId(Long titularId);

}
