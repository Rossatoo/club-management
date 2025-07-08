package com.tcc.club_management.associado.service;

import com.tcc.club_management.associado.model.Associado;
import com.tcc.club_management.associado.repository.AssociadoRepositoy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssociadoService {

    private final AssociadoRepositoy associadoRepositoy;

    public AssociadoService(AssociadoRepositoy associadoRepositoy) {
        this.associadoRepositoy = associadoRepositoy;
    }

    public List<Associado> listarTodos(){
        return associadoRepositoy.findAll();
    }

    public Associado salvar(Associado associado){
        return associadoRepositoy.save(associado);
    }

    public Associado buscarPorId(Long id){
        return associadoRepositoy.findById(id).orElse(null);
    }

    public void deletar(Long id){
        associadoRepositoy.deleteById(id);
    }

    public List<Associado> buscarDependentes(Long titularId){
        return associadoRepositoy.findByTitularId(titularId);
    }

}
