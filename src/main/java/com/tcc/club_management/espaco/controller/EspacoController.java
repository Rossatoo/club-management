package com.tcc.club_management.espaco.controller;

import com.tcc.club_management.espaco.model.Espaco;
import com.tcc.club_management.espaco.service.EspacoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
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

    @GetMapping("/buscar")
    public Page<Espaco> buscarPorNome(
            @RequestParam("nome") String nome,
            Pageable pageable){
        return espacoService.buscarPorNome(nome, pageable);
    }

    @DeleteMapping("/{id}")
    public void deletarEspaco(@PathVariable Long id){
        espacoService.deletar(id);
    }
}
