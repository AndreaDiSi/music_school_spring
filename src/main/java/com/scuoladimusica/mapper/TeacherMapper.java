package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.request.TeacherRequest;
import com.scuoladimusica.model.dto.response.TeacherResponse;
import com.scuoladimusica.model.entity.Teacher;
@Component
public class TeacherMapper {

    public TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getMatricolaInsegnante(),
                teacher.getCf(),
                teacher.getNome(),
                teacher.getCognome(),
                teacher.getNomeCompleto(),
                teacher.getDataNascita(),
                teacher.getTelefono(),
                teacher.getStipendio(),
                teacher.getSpecializzazione(),
                teacher.getAnniEsperienza(),
                teacher.getCourses().size()
        );
    }

    public Teacher toEntity(TeacherRequest request) {
        return Teacher.builder()
                .matricolaInsegnante(request.matricolaInsegnante())
                .cf(request.cf())
                .nome(request.nome())
                .cognome(request.cognome())
                .dataNascita(request.dataNascita())
                .telefono(request.telefono())
                .stipendio(request.stipendio())
                .specializzazione(request.specializzazione())
                .anniEsperienza(request.anniEsperienza())
                .build();
    }

    public List<TeacherResponse> toTeacherResponses(List<Teacher> teachers) {
        List<TeacherResponse> teacherResponses = new ArrayList<>();
        for (Teacher teacher : teachers) {
            teacherResponses.add(toResponse(teacher));
        }
        return teacherResponses;
    }
}
