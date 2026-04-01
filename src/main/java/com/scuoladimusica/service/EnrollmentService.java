package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.mapper.EnrollmentMapper;
import com.scuoladimusica.model.dto.response.EnrollmentResponse;
import com.scuoladimusica.model.entity.Enrollment;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.EnrollmentRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;

    private StudentRepository studentRepository;

    private CourseRepository courseRepository;

    private EnrollmentMapper enrollmentMapper;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
            CourseRepository courseRepository, EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Transactional
    public EnrollmentResponse enrollStudent(String matricola, String codiceCorso, int anno) {
        var studente = studentRepository.findByMatricola(matricola)
                .orElseThrow(() -> new ResourceNotFoundException("studente not found"));
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("corso not found"));

        if (enrollmentRepository.existsByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso)) {
            throw new DuplicateResourceException("Studente already in corso");
        }

        var enrollment = Enrollment.builder()
                .student(studente)
                .course(corso)
                .annoIscrizione(anno)
                .votoFinale(null).build();

        enrollmentRepository.save(enrollment);

        return enrollmentMapper.toResponse(enrollment);
    }

    @Transactional
    public EnrollmentResponse registerVote(String matricola, String codiceCorso, int voto) {
        var enrollment = enrollmentRepository.findByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment non trovato"));

        if (voto >= 18 && voto <= 30) {
            enrollment.setVotoFinale(voto);
            return enrollmentMapper.toResponse(enrollment);
        } else {
            throw new BusinessRuleException("voto non può essere minore di 18 o maggiore di 30");
        }
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(String matricola) {
        var student = studentRepository.findByMatricola(matricola)
                .orElseThrow(() -> new ResourceNotFoundException("studente not found"));
        return enrollmentMapper.toEnrollmentResponses(enrollmentRepository.findByStudentId(student.getId()));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(String codiceCorso) {
        var course = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("corso not found"));
        return enrollmentMapper.toEnrollmentResponses(enrollmentRepository.findByCourseId(course.getId()));
    }
}
