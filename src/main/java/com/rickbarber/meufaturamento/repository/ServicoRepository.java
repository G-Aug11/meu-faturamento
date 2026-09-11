package com.rickbarber.meufaturamento.repository;

import com.rickbarber.meufaturamento.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByAtivoTrueOrderByIdAsc();
}
