package com.tcc.club_management.associado.controller;

import com.tcc.club_management.associado.model.Associado;
import com.tcc.club_management.associado.service.AssociadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/associados")
public class AssociadoController {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @GetMapping
    public List<Associado> listarAssociados(){
        return associadoService.listarTodos();
    }

    @PostMapping
    public Associado cadastrarAssociado(@RequestBody Associado associado){
        return associadoService.salvar(associado);
    }

    @GetMapping("/{id}")
    public Associado buscarAssociado(@PathVariable Long id){
        return associadoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarAssociado(@PathVariable Long id){
        associadoService.deletar(id);
    }

    @GetMapping("/{id}/dependentes")
    public List<Associado> listarDependentes(@PathVariable Long id){
        return associadoService.buscarDependentes(id);
    }

}
