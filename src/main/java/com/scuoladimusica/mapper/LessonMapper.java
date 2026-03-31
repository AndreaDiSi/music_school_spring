package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.request.LessonRequest;
import com.scuoladimusica.model.dto.response.LessonResponse;
import com.scuoladimusica.model.entity.Lesson;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getNumero(),
                lesson.getData(),
                lesson.getOraInizio(),
                lesson.getDurata(),
                lesson.getAula(),
                lesson.getArgomento()
        );
    }

    // Il corso va impostato dal service dopo la creazione
    public Lesson toEntity(LessonRequest request) {
        return Lesson.builder()
                
                .numero(request.numero())
                .data(request.data())
                .oraInizio(request.oraInizio())
                .durata(request.durata())
                .aula(request.aula())
                .argomento(request.argomento())
                .build();
    }

    public List<LessonResponse> toLessonResponses(List<Lesson> lessons) {
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            lessonResponses.add(toResponse(lesson));
        }
        return lessonResponses;
    }
}
