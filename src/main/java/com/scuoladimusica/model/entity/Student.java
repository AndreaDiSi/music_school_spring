package com.scuoladimusica.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends SuperUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La matricola non può essere vuota")
    @Column(unique = true, nullable = false)
    private String matricola;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Livello livello = Livello.PRINCIPIANTE;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    public String getNomeCompleto() {
        return getNome() + " " + getCognome();
    }

    public int getNumeroCorsiFrequentati() {
        return enrollments.size();
    }

    public double getMediaVoti() {
        List<Integer> voti = enrollments.stream()
                .map(Enrollment::getVotoFinale)
                .filter(v -> v != null)
                .toList();

        if (voti.isEmpty()) return 0.0;

        return voti.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}
