package com.rickbarber.agendamento.repository;

import com.rickbarber.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);

    // Usado pra não deixar marcar dois clientes no mesmo horário
    boolean existsByDataHoraAndStatus(LocalDateTime dataHora, String status);
}
