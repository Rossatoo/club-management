package com.tcc.club_management.reserva;

import com.tcc.club_management.associado.Associado;
import com.tcc.club_management.associado.AssociadoRepository;
import com.tcc.club_management.espaco.CategoriaEspaco;
import com.tcc.club_management.espaco.Espaco;
import com.tcc.club_management.espaco.EspacoRepository;
import com.tcc.club_management.usuario.Usuario;
import com.tcc.club_management.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    private static final long CREATE_MIN_HOURS = 2;
    private static final long CANCEL_WINDOW_HOURS = 12;
    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);
    private static final long BUFFER_MINUTES = 0;
    private static final long BLOCO_MINUTOS = 30;

    private final ReservaRepository reservaRepository;
    private final EspacoRepository espacoRepository;
    private final AssociadoRepository associadoRepositoy;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          EspacoRepository espacoRepository,
                          AssociadoRepository associadoRepositoy,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.espacoRepository = espacoRepository;
        this.associadoRepositoy = associadoRepositoy;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id){
        return reservaRepository.findById(id).get();
    }

    public void deletar(Long id){
        reservaRepository.deleteById(id);
    }

    public List<Reserva> listarPorAssociado(Long associadoId){return reservaRepository.findByAssociado_Id(associadoId); }

    public List<Reserva> listarPorEspaco(Long espacoId){
        return reservaRepository.findByEspaco_Id(espacoId);
    }

    // ================= REGRAS DE NEGOCIO ===================

    @Transactional
    public ReservaResponseDTO criarReserva(ReservaRequestDTO req, Authentication auth){
        if (req.getEspacoId() == null) throw new IllegalArgumentException("espacoId eh obrigatorio");
        if (req.getDataReserva() == null) throw new IllegalArgumentException("dataReserva eh obrigatorio");
        if (req.getHoraFim() == null || req.getHoraFim() == null) throw new IllegalArgumentException("Horarios sao obrigatorios");
        if (!req.getHoraInicio().isBefore(req.getHoraFim())) throw new IllegalArgumentException("horaInicio deve ser antes de horaFim");

        Espaco espaco = espacoRepository.findById(req.getEspacoId()).orElseThrow(() -> new IllegalArgumentException("Espaco nao encontrado"));

        if (Boolean.FALSE.equals(espaco.getDisponivel())) {
            throw new IllegalStateException("Espaco indisponivel para reserva");
        }

        validarFuncionamento(req.getHoraInicio(), req.getHoraFim());
        validarDatas(req.getDataReserva(), req.getHoraInicio());

        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isSocio = hasRole(auth, "ROLE_SOCIO");
        boolean isComum = hasRole(auth, "ROLE_COMUM");

        // Permissao por Categoria
        if (espaco.getCategoria() == CategoriaEspaco.ESPORTIVA){
            if (!(isAdmin|| isSocio)) {
                throw new AccessDeniedException("Apenas sócios podem reservar espacos esportivos");
            }
        } else if (espaco.getCategoria() == CategoriaEspaco.FESTA) {
        }

        // Dono da Reserva
        Associado associado = null;
        Usuario usuarioComum = null;

        if (isAdmin && req.getAssociadoId() != null) {
            associado = associadoRepositoy.findById(req.getAssociadoId())
                    .orElseThrow(() -> new IllegalArgumentException("Associado nao encontrado"));
        } else {
            String email = auth.getName();
            if (isSocio) {
                associado = associadoRepositoy.findByUsuario_Email(email)
                        .orElseThrow(() -> new IllegalArgumentException("Associado nao encontrado"));
            } else {
                usuarioComum = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
            }
        }

        LocalTime inicio = req.getHoraInicio().minusMinutes(BUFFER_MINUTES);
        LocalTime fim = req.getHoraFim().plusMinutes(BUFFER_MINUTES);

        //Conflito
        boolean conflito = reservaRepository.existsOverlap(
                espaco.getId(), req.getDataReserva(), inicio, fim
        );
        if (conflito) {
            throw new IllegalStateException("Ja existe reserva nesse horario para este espaco");
        }

        // Calculo de Preco
        BigDecimal precoAplicado = calcularPreco(espaco, isSocio || associado != null, req.getHoraInicio(), req.getHoraFim());

        Reserva r = new  Reserva();
        r.setAssociado(associado);
        r.setUsuario(usuarioComum);
        r.setEspaco(espaco);
        r.setDataReserva(req.getDataReserva());
        r.setHoraInicio(req.getHoraInicio());
        r.setHoraFim(req.getHoraFim());
        r.setStatus(StatusReserva.PENDENTE);
        r.setObservacao(req.getObservacao());

        Reserva salvo = reservaRepository.save(r);
        return toDTO(salvo);
    }

    // Confirmação de Reserva (ADMIN)
    @Transactional
    public ReservaResponseDTO confirmarReserva(Long reservaId, Authentication auth){
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Apenas administradores podem confirmar reservas");
        }

        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada"));

        if (r.getStatus() == StatusReserva.CANCELADA) {
            throw new IllegalStateException("Reserva cancelada não pode ser confirmada");
        }
        if (r.getStatus() == StatusReserva.CONFIRMADA) {
            return toDTO(r);
        }

        r.setStatus(StatusReserva.CONFIRMADA);
        return toDTO(reservaRepository.save(r));
    }

    // Cancelar Reserva (Dono ou ADMIN)
    @Transactional
    public ReservaResponseDTO cancelarReserva(Long reservaId, Authentication auth){
        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada"));

        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isDono = isDonoDaReserva(r, auth);

        if (!(isAdmin || isDono)) {
            throw new AccessDeniedException("Voce nao pode cancelar essa reserva");
        }

        if(!isAdmin) {
            LocalDateTime inicio = LocalDateTime.of(r.getDataReserva(), r.getHoraInicio());
            if (Duration.between(LocalDateTime.now(), inicio).toHours() < CANCEL_WINDOW_HOURS) {
                throw new IllegalStateException("Prazo de cancelamento excedido!");
            }
        }
        if (r.getStatus() == StatusReserva.CANCELADA) {
            return toDTO(r);
        }

        r.setStatus(StatusReserva.CANCELADA);
        return toDTO(reservaRepository.save(r));
    }


    // =========== VALIDAÇÕES ====================
    private void validarFuncionamento(LocalTime inicio, LocalTime fim) {
        // Se quiser horários por espaço, mova OPEN/CLOSE para Espaco
        if (inicio.isBefore(OPEN_TIME) || fim.isAfter(CLOSE_TIME)) {
            throw new IllegalArgumentException("Horário fora do funcionamento do espaço ("
                    + OPEN_TIME + "–" + CLOSE_TIME + ")");
        }
    }

    private void validarDatas(LocalDate dataReserva, LocalTime horaInicio) {
        if (dataReserva.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data da reserva no passado");
        }
        // antecedência mínima (aplica para data de hoje)
        LocalDateTime inicio = LocalDateTime.of(dataReserva, horaInicio);
        long horasAteInicio = Duration.between(LocalDateTime.now(), inicio).toHours();
        if (horasAteInicio < CREATE_MIN_HOURS) {
            throw new IllegalArgumentException("A reserva deve ser feita com pelo menos "
                    + CREATE_MIN_HOURS + " horas de antecedência");
        }
    }

    private boolean hasRole(Authentication auth, String role){
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }

    private BigDecimal calcularPreco(Espaco espaco, boolean socio, LocalTime inicio, LocalTime fim){
        if (espaco.getCategoria() == CategoriaEspaco.ESPORTIVA) {
            long minutos = Duration.between(inicio, fim).toMinutes();
            long blocos = (minutos + (BLOCO_MINUTOS - 1)) / BLOCO_MINUTOS;
            BigDecimal horas = BigDecimal.valueOf(blocos)
                    .multiply(BigDecimal.valueOf(BLOCO_MINUTOS))
                    .divide(BigDecimal.valueOf(60) , 2, RoundingMode.HALF_UP);
            BigDecimal precoHora = socio ? nvl(espaco.getPrecoHoraSocio()) : nvl(espaco.getPrecoHoraComum());
            return horas.multiply(precoHora).setScale(2, RoundingMode.HALF_UP);
        } else {
            BigDecimal precoDia = socio ? nvl(espaco.getPrecoDiaSocio()) : nvl(espaco.getPrecoDiaComum());
            return precoDia.setScale(2, RoundingMode.HALF_UP);
        }
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private boolean isDonoDaReserva(Reserva r, Authentication auth) {
        String email = auth.getName();
        // Se tiver associado: compara com o associado do usuário logado
        if (r.getAssociado() != null && r.getAssociado().getUsuario() != null) {
            return email.equalsIgnoreCase(r.getAssociado().getUsuario().getEmail());
        }
        // Se reserva foi feita por COMUM e vinculada direto ao Usuario:
        if (r.getUsuario() != null) {
            return email.equalsIgnoreCase(r.getUsuario().getEmail());
        }
        return false;
    }

    // ============ MAPEAMENTO DTO ===========
    public ReservaResponseDTO toDTO(Reserva r){
        return new ReservaResponseDTO(
                r.getId(),
                r.getAssociado() != null ? r.getAssociado().getId() : null,
                r.getUsuario() != null ? r.getUsuario().getId() : null,
                r.getEspaco() != null ? r.getEspaco().getId() : null,
                r.getEspaco() != null ? r.getEspaco().getNome() : null,
                r.getEspaco() != null ? r.getEspaco().getCategoria() : null,
                r.getDataReserva(),
                r.getHoraInicio(),
                r.getHoraFim(),
                r.getStatus(),
                r.getObservacao()
        );
    }
}
