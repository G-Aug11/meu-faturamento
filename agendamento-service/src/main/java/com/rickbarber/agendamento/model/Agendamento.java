package com.rickbarber.agendamento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Esse serviço tem banco próprio, então não dá pra fazer FK pra tabela clientes
    // do meu-faturamento. Guardamos só o id e uma cópia do nome pra exibir.
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "servico_id", nullable = false)
    private Long servicoId;

    @Column(name = "nome_servico", nullable = false, length = 80)
    private String nomeServico;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // AGENDADO, CONCLUIDO ou CANCELADO
    @Column(nullable = false, length = 20)
    private String status = "AGENDADO";

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Long getId()                { return id; }
    public Long getClienteId()         { return clienteId; }
    public String getNomeCliente()     { return nomeCliente; }
    public Long getServicoId()         { return servicoId; }
    public String getNomeServico()     { return nomeServico; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getStatus()          { return status; }
    public String getObservacao()      { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setClienteId(Long clienteId)         { this.clienteId = clienteId; }
    public void setNomeCliente(String nomeCliente)   { this.nomeCliente = nomeCliente; }
    public void setServicoId(Long servicoId)         { this.servicoId = servicoId; }
    public void setNomeServico(String nomeServico)   { this.nomeServico = nomeServico; }
    public void setDataHora(LocalDateTime dataHora)  { this.dataHora = dataHora; }
    public void setStatus(String status)             { this.status = status; }
    public void setObservacao(String observacao)     { this.observacao = observacao; }
}
