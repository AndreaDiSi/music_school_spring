package com.scuoladimusica.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SuperUser {

    @NotBlank(message = "Il codice fiscale non può essere vuoto")
    @Size(min = 16, max = 16, message = "Il codice fiscale deve essere di 16 caratteri")
    @Column(nullable = false)
    private String cf;

    @NotBlank(message = "Il nome non può essere vuoto")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Column(nullable = false)
    private String cognome;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    @Column(nullable = false)
    private LocalDate dataNascita;

    private String telefono;
}
