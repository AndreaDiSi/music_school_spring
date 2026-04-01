package com.scuoladimusica.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Teacher extends SuperUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La matricola insegnante non può essere vuota")
    @Column(unique = true, nullable = false)
    private String matricolaInsegnante;

    @NotNull(message = "Lo stipendio è obbligatorio")
    @Positive(message = "Lo stipendio deve essere positivo")
    @Column(nullable = false)
    private Double stipendio;

    @NotBlank(message = "La specializzazione non può essere vuota")
    @Column(nullable = false)
    private String specializzazione;

    @Min(value = 0, message = "Gli anni di esperienza non possono essere negativi")
    @Column(nullable = false)
    @Builder.Default
    private int anniEsperienza = 0;

    @OneToMany(mappedBy = "teacher")
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    public String getNomeCompleto() {
        return getNome() + " " + getCognome();
    }
}
