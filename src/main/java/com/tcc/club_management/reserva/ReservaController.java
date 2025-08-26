package com.tcc.club_management.reserva;

import com.tcc.club_management.associado.Associado;
import com.tcc.club_management.associado.AssociadoRepository;
import com.tcc.club_management.reserva.ReservaRequestDTO;
import com.tcc.club_management.reserva.ReservaResponseDTO;
import com.tcc.club_management.usuario.Usuario;
import com.tcc.club_management.usuario.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final AssociadoRepository associadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public ReservaController(ReservaService reservaService,
                             AssociadoRepository associadoRepository,
                             UsuarioRepository usuarioRepository,
                             ReservaRepository reservaRepository) {
        this.reservaService = reservaService;
        this.associadoRepository = associadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    // ===================== Criação =====================

    /**
     * Criar reserva (COMUM/SOCIO/ADMIN). Regras por categoria e conflitos são aplicadas no service.
     * - ADMIN pode enviar associadoId no body para reservar por terceiros.
     * - COMUM/SOCIO: associado/usuario é inferido pelo JWT.
     */
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@Validated @RequestBody ReservaRequestDTO dto,
                                                                                    Authentication auth) {
        ReservaResponseDTO created = reservaService.criarReserva(dto, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ===================== Leitura =====================

    /**
     * Listagem geral (ADMIN). A regra de acesso está na SecurityConfig (hasRole('ADMIN')).
     */
    @GetMapping
    public List<ReservaResponseDTO> listarTodas() {
        return reservaService.listarTodas().stream()
                .map(reservaService::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Minhas reservas:
     * - SOCIO: busca pelo associado vinculado ao e-mail do JWT.
     * - COMUM: busca pelo usuário vinculado ao e-mail do JWT.
     * - ADMIN: pode retornar tudo ou, se preferir, vazio (aqui retornamos tudo para admin).
     */
    @GetMapping("/minhas")
    public List<ReservaResponseDTO> minhas(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSocio = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SOCIO"));
        String email = auth.getName();

        if (isAdmin) {
            return reservaService.listarTodas().stream()
                    .map(reservaService::toDTO)
                    .collect(Collectors.toList());
        }

        if (isSocio) {
            Associado associado = associadoRepository.findByUsuario_Email(email)
                    .orElseThrow(() -> new NoSuchElementException("Associado não encontrado"));
            return reservaService.listarPorAssociado(associado.getId()).stream()
                    .map(reservaService::toDTO)
                    .collect(Collectors.toList());
        }

        // COMUM
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
        // Requer o método no ReservaRepository: List<Reserva> findByUsuario_Id(Long id)
        return reservaRepository.findByUsuario_Id(user.getId()).stream()
                .map(reservaService::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Detalhe da reserva. Permite se for ADMIN ou dono da reserva.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> detalhe(@PathVariable Long id, Authentication auth) {
        Reserva r = reservaService.buscarPorId(id);
        if (r == null) return ResponseEntity.notFound().build();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !isDonoDaReserva(r, auth)) {
            throw new AccessDeniedException("Você não pode visualizar esta reserva");
        }
        return ResponseEntity.ok(reservaService.toDTO(r));
    }

    /**
     * Lista reservas por espaço; opcionalmente filtra por data (YYYY-MM-DD).
     * - Útil para mostrar disponibilidade/agenda do espaço.
     */
    @GetMapping("/espaco/{espacoId}")
    public List<ReservaResponseDTO> listarPorEspaco(@PathVariable Long espacoId,
                                                    @RequestParam(required = false) String data) {
        return reservaService.listarPorEspaco(espacoId).stream()
                .filter(r -> data == null || r.getDataReserva().toString().equals(data))
                .map(reservaService::toDTO)
                .collect(Collectors.toList());
    }

    // ===================== Ações de estado =====================

    /**
     * Confirmar reserva (ADMIN).
     */
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id, Authentication auth) {
        ReservaResponseDTO dto = reservaService.confirmarReserva(id, auth);
        return ResponseEntity.ok(dto);
    }

    /**
     * Cancelar reserva (ADMIN ou dono).
     * - Dono: respeita janela de cancelamento (configurada no service).
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id, Authentication auth) {
        ReservaResponseDTO dto = reservaService.cancelarReserva(id, auth);
        return ResponseEntity.ok(dto);
    }

    // ===================== Exclusão =====================

    /**
     * Excluir reserva (recomendado apenas ADMIN). Se preferir, desative e deixe só cancelar().
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) throw new AccessDeniedException("Apenas administradores podem excluir reservas");
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== Helpers =====================

    private boolean isDonoDaReserva(Reserva r, Authentication auth) {
        String email = auth.getName();
        if (r.getAssociado() != null && r.getAssociado().getUsuario() != null) {
            return email.equalsIgnoreCase(r.getAssociado().getUsuario().getEmail());
        }
        if (r.getUsuario() != null) {
            return email.equalsIgnoreCase(r.getUsuario().getEmail());
        }
        return false;
    }

    // ===================== Tratamento de erros comuns =====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /** Conflito de horário, janela de cancelamento etc. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleForbidden(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
