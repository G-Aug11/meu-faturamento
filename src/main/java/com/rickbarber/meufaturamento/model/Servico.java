package com.rickbarber.meufaturamento.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
public class Servico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String nome;
    @Column(length = 20)
    private String icone = "✂️";
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal preco;
    @Column(nullable = false)
    private Boolean ativo = true;

    public Long getId()          { return id; }
    public String getNome()      { return nome; }
    public String getIcone()     { return icone; }
    public BigDecimal getPreco() { return preco; }
    public Boolean getAtivo()    { return ativo; }
    public void setId(Long id)             { this.id = id; }
    public void setNome(String nome)       { this.nome = nome; }
    public void setIcone(String icone)     { this.icone = icone; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public void setAtivo(Boolean ativo)    { this.ativo = ativo; }
}
