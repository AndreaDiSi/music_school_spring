package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.response.EnrollmentResponse;
import com.scuoladimusica.model.entity.Enrollment;

@Component
public class EnrollmentMapper {


    public EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getMatricola(),
                enrollment.getStudent().getNomeCompleto(),
                enrollment.getCourse().getCodiceCorso(),
                enrollment.getCourse().getNome(),
                enrollment.getAnnoIscrizione(),
                enrollment.getVotoFinale()
        );
    }

    public List<EnrollmentResponse> toEnrollmentResponses(List<Enrollment> enrollments){

        List<EnrollmentResponse> enrollmentResponses = new ArrayList<>();
       for (Enrollment enrollment : enrollments) {
        enrollmentResponses.add(toResponse(enrollment));
       }
       return enrollmentResponses;
    }
}
