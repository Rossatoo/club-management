package com.tcc.club_management.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
