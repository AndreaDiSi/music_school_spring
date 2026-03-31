package com.scuoladimusica.mapper;

import com.scuoladimusica.model.dto.request.CourseRequest;
import com.scuoladimusica.model.dto.response.CourseResponse;
import com.scuoladimusica.model.dto.response.LessonResponse;
import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Livello;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        
        return new CourseResponse(
                course.getId(),
                course.getCodiceCorso(),
                course.getNome(),
                course.getDataInizio(),
                course.getDataFine(),
                course.getCostoOrario(),
                course.getTotaleOre(),
                course.getCostoTotale(),
                course.getDurataGiorni(),
                course.isOnline(),
                course.getLivello(),
                course.getTeacher() != null ? course.getTeacher().getNome() : null,
                course.getTeacher() != null ? course.getTeacher().getCognome() : null,
                course.getEnrollments().size(),
                course.getLessons().stream()
                        .map(l -> new LessonResponse(l.getId(), l.getNumero(), l.getData(), l.getOraInizio(), l.getDurata(), l.getAula(), l.getArgomento()))
                        .toList()
        );
    }

    // Il teacher va impostato dal service dopo la creazione
    public Course toEntity(CourseRequest request) {
        return Course.builder()
                .codiceCorso(request.codiceCorso())
                .nome(request.nome())
                .dataInizio(request.dataInizio())
                .dataFine(request.dataFine())
                .costoOrario(request.costoOrario())
                .totaleOre(request.totaleOre())
                .online(request.online())
                .livello(request.livello() != null ? request.livello() : Livello.PRINCIPIANTE)
                .build();
    }

    public List<CourseResponse> toCourseResponses(List<Course> courses){

        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : courses) {
            courseResponses.add(toResponse(course));
        }
        return courseResponses;
    }
}
