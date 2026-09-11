package com.rickbarber.meufaturamento.repository;

import com.rickbarber.meufaturamento.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(a.total), 0) FROM Atendimento a WHERE a.dataHora BETWEEN :inicio AND :fim")
    BigDecimal totalPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Atendimento a WHERE a.dataHora BETWEEN :inicio AND :fim")
    Long countPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "SELECT s.nome, s.icone, COUNT(*) AS qtd, SUM(s.preco) AS total FROM atendimento_servicos ats JOIN servicos s ON s.id = ats.servico_id GROUP BY s.id, s.nome, s.icone ORDER BY qtd DESC LIMIT 5", nativeQuery = true)
    List<Object[]> topServicos();

    @Query(value = "SELECT a.forma_pagamento, COUNT(*) AS qtd, SUM(a.total) AS total FROM atendimentos a WHERE a.data_hora BETWEEN :inicio AND :fim AND a.forma_pagamento IS NOT NULL GROUP BY a.forma_pagamento", nativeQuery = true)
    List<Object[]> formasPagamentoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = "SELECT DATE(a.data_hora) AS dia, COUNT(*) AS qtd, SUM(a.total) AS total FROM atendimentos a WHERE a.data_hora >= :inicio GROUP BY DATE(a.data_hora) ORDER BY dia", nativeQuery = true)
    List<Object[]> ultimos7Dias(@Param("inicio") LocalDateTime inicio);
}
