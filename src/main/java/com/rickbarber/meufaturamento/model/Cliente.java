package com.rickbarber.meufaturamento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;
    @Column(length = 20)
    private String telefone;
    @Column(columnDefinition = "TEXT")
    private String observacao;
    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Long getId()                { return id; }
    public String getNome()            { return nome; }
    public String getTelefone()        { return telefone; }
    public String getObservacao()      { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setId(Long id)                       { this.id = id; }
    public void setNome(String nome)                 { this.nome = nome; }
    public void setTelefone(String telefone)         { this.telefone = telefone; }
    public void setObservacao(String observacao)     { this.observacao = observacao; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
