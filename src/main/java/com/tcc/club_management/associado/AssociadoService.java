package com.tcc.club_management.associado;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssociadoService {

    private final AssociadoRepository associadoRepositoy;

    public AssociadoService(AssociadoRepository associadoRepositoy) {
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
