package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.request.StudentRequest;
import com.scuoladimusica.model.dto.response.StudentResponse;
import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.model.entity.Student;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getMatricola(),
                student.getCf(),
                student.getNome(),
                student.getCognome(),
                student.getNomeCompleto(),
                student.getDataNascita(),
                student.getTelefono(),
                student.getLivello(),
                student.getNumeroCorsiFrequentati(),
                student.getMediaVoti()
        );
    }

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .matricola(request.matricola())
                .cf(request.cf())
                .nome(request.nome())
                .cognome(request.cognome())
                .dataNascita(request.dataNascita())
                .telefono(request.telefono())
                .livello(request.livello() != null ? request.livello() : Livello.PRINCIPIANTE)
                .build();
    }

    public List<StudentResponse> toStudentResponses(List<Student> students) {
        List<StudentResponse> studentResponses = new ArrayList<>();
        for (Student student : students) {
            studentResponses.add(toResponse(student));
        }
        return studentResponses;
    }
}
