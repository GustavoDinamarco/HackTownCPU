package com.model.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Palestrante extends Pessoa{

    private String descricao;
    private String fotoUrl;

    @ManyToMany(mappedBy = "palestrantes")
    private List<Evento> eventos = new ArrayList<>();
}
