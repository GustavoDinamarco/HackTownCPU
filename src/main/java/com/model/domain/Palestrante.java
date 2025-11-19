package com.model.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "palestrantes")
@PrimaryKeyJoinColumn(name = "id")
public class Palestrante extends Pessoa{

    private String descricao;
    
    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToMany(mappedBy = "palestrantes")
    private List<Evento> eventos = new ArrayList<>();
}
