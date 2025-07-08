package com.tcc.club_management.espaco.service;

import com.tcc.club_management.espaco.model.Espaco;
import com.tcc.club_management.espaco.repository.EspacoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;

    public EspacoService(EspacoRepository espacoRepository) {
        this.espacoRepository = espacoRepository;
    }

    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }

    public Espaco salvar(Espaco espaco) {
        return  espacoRepository.save(espaco);
    }

    public Espaco buscarPorId(Long id){
        return espacoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id){
        espacoRepository.deleteById(id);
    }
}
