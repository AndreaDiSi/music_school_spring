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

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Transactional
    public EnrollmentResponse enrollStudent(String matricola, String codiceCorso, int anno) {

        if(studentRepository.existsByMatricola(matricola)){
            var studente = studentRepository.findByMatricola(matricola).get();

            if(courseRepository.existsByCodiceCorso(codiceCorso)){
                var corso = courseRepository.findByCodiceCorso(codiceCorso).get();

                if(enrollmentRepository.existsByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso)){
                    throw new DuplicateResourceException("Studente already in corso");
                }

                var enrollment = Enrollment.builder()
                        .student(studente)
                        .course(corso)
                        .annoIscrizione(anno)
                        .votoFinale(null).build();

                enrollmentRepository.save(enrollment);

                return enrollmentMapper.toResponse(enrollment);

            } else {
                throw new ResourceNotFoundException("corso not found");
            }
        } else {
            throw new ResourceNotFoundException("studente not found");
        }
    }

    @Transactional
    public EnrollmentResponse registerVote(String matricola, String codiceCorso, int voto) {

        if(enrollmentRepository.existsByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso)){
            try {
                var enrollment = enrollmentRepository.findByStudentMatricolaAndCourseCodiceCorso(matricola, codiceCorso).get();
                if(voto >= 18 && voto <= 30){
                    enrollment.setVotoFinale(voto);
                    return enrollmentMapper.toResponse(enrollment);
                } else {
                    throw new BusinessRuleException("voto non può essere minore di 18 o maggiore di 30");
                }
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("Enrollment non trovato");
            }
        } else {
            throw new ResourceNotFoundException("enrollment non trovato");
        }
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(String matricola) {

        if(studentRepository.existsByMatricola(matricola)){
            var student = studentRepository.findByMatricola(matricola).get();
            var enrollments = enrollmentRepository.findByStudentId(student.getId()) ;
            return enrollmentMapper.toEnrollmentResponses(enrollments);
        } else {
            throw new ResourceNotFoundException("studente not found");
        }
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(String codiceCorso) {
        if(courseRepository.existsByCodiceCorso(codiceCorso)){
            var course = courseRepository.findByCodiceCorso(codiceCorso).get();
            return enrollmentMapper.toEnrollmentResponses(enrollmentRepository.findByCourseId(course.getId()));
        } else {
            throw new ResourceNotFoundException("corso not found");
        }
    }
}
