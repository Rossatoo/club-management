package com.tcc.club_management.reserva.service;

import com.tcc.club_management.reserva.model.Reserva;
import com.tcc.club_management.reserva.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Reserva salvar(Reserva reserva) {
        return  reservaRepository.save(reserva);
    }

    public Reserva buscarPorId(Long id){
        return reservaRepository.findById(id).get();
    }

    public void deletar(Long id){
        reservaRepository.deleteById(id);
    }

    public List<Reserva> listarPorAssociado(Long associadoId){
        return reservaRepository.findByAssociadoId(associadoId);
    }

    public List<Reserva> listarPorEspaco(Long espacoId){
        return reservaRepository.findByEspacoId(espacoId);
    }

}
