package com.tcc.club_management.espaco.controller;

import com.tcc.club_management.espaco.model.Espaco;
import com.tcc.club_management.espaco.service.EspacoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/espacos")
public class EspacoController {

    private final EspacoService espacoService;

    public EspacoController(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    @GetMapping
    public List<Espaco> listarEspacos(){
        return espacoService.listarTodos();
    }

    @PostMapping
    public Espaco cadastrarEspaco(@RequestBody Espaco espaco){
        return espacoService.salvar(espaco);
    }

    @GetMapping("/{id}")
    public Espaco buscarEspaco(@PathVariable Long id){
        return  espacoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarEspaco(@PathVariable Long id){
        espacoService.deletar(id);
    }
}
