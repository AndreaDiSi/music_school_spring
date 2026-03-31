package com.scuoladimusica.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scuoladimusica.model.entity.Livello;

import io.micrometer.common.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

public record CourseResponse(
        Long id,
        String codiceCorso,
        String nome,
        LocalDate dataInizio,
        LocalDate dataFine,
        Double costoOrario,
        Integer totaleOre,
        double costoTotale,
        long durataGiorni,
        boolean online,
        Livello livello,
        @Nullable
        @JsonIgnore
        String nomeInsegnante,
        String cognomeInsegnante,
        int numeroIscritti,
        List<LessonResponse> lezioni
) {
    @JsonProperty("nomeInsegnante")
    public String nomeInsegnanteCompleto() {
        return nomeInsegnante != null ? nomeInsegnante + " " + cognomeInsegnante : null;
    }
}
