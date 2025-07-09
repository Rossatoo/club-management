package com.tcc.club_management.espaco.repository;

import com.tcc.club_management.espaco.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface EspacoRepository extends JpaRepository<Espaco, Long> {
}
