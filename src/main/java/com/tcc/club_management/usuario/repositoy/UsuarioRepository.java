package com.tcc.club_management.usuario.repositoy;

import com.tcc.club_management.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
