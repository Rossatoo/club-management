package com.tcc.club_management.espaco;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Espaco> buscarPorNome(String nome, Pageable pageable) {
        return espacoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public void deletar(Long id){
        espacoRepository.deleteById(id);
    }
}
