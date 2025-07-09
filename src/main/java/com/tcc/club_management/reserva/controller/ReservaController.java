package com.tcc.club_management.reserva.controller;

import com.tcc.club_management.reserva.model.Reserva;
import com.tcc.club_management.reserva.service.ReservaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listarReservas(){
        return reservaService.listarTodas();
    }

    @PostMapping
    public Reserva adicionarReserva(@RequestBody Reserva reserva){
        return reservaService.salvar(reserva);
    }

    @GetMapping("/{id}")
    public Reserva buscarPorId(@PathVariable Long id){
        return reservaService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarReserva(@PathVariable Long id){
        reservaService.deletar(id);
    }

    @GetMapping("/associado/{id}")
    public List<Reserva> listarPorAssociado(@PathVariable Long id){
        return reservaService.listarPorAssociado(id);
    }

    @GetMapping("/espaco/{id}")
    public List<Reserva> listarPorEspaco(@PathVariable Long id){
        return  reservaService.listarPorEspaco(id);
    }

}
