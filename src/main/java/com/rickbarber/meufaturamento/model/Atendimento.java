package com.rickbarber.meufaturamento.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "atendimentos")
public class Atendimento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @Column(name = "nome_avulso", length = 100)
    private String nomeAvulso;
    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    @Column(columnDefinition = "TEXT")
    private String observacao;
    @Column(name = "forma_pagamento", length = 20)
    private String formaPagamento;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "atendimento_servicos",
        joinColumns = @JoinColumn(name = "atendimento_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id"))
    private List<Servico> servicos;

    public Long getId()                { return id; }
    public Cliente getCliente()        { return cliente; }
    public String getNomeAvulso()      { return nomeAvulso; }
    public LocalDateTime getDataHora() { return dataHora; }
    public BigDecimal getTotal()       { return total; }
    public String getObservacao()      { return observacao; }
    public String getFormaPagamento()  { return formaPagamento; }
    public List<Servico> getServicos() { return servicos; }
    public void setId(Long id)                      { this.id = id; }
    public void setCliente(Cliente cliente)         { this.cliente = cliente; }
    public void setNomeAvulso(String nomeAvulso)    { this.nomeAvulso = nomeAvulso; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public void setTotal(BigDecimal total)          { this.total = total; }
    public void setObservacao(String obs)           { this.observacao = obs; }
    public void setFormaPagamento(String forma)     { this.formaPagamento = forma; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    public String getNomeCliente() {
        if (cliente != null && cliente.getNome() != null) return cliente.getNome();
        if (nomeAvulso != null && !nomeAvulso.isBlank()) return nomeAvulso;
        return "Avulso";
    }
}
