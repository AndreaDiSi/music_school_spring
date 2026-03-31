package com.scuoladimusica.service;

import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.mapper.StudentMapper;
import com.scuoladimusica.model.dto.request.StudentRequest;
import com.scuoladimusica.model.dto.response.StudentReportResponse;
import com.scuoladimusica.model.dto.response.StudentResponse;
import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.model.entity.Student;
import com.scuoladimusica.repository.EnrollmentRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private StudentMapper studentMapper = new StudentMapper();

    public StudentResponse createStudent(StudentRequest request) {

        if(studentRepository.existsByMatricola(request.matricola())){
            throw new DuplicateResourceException("Uno studente con questa matricola già esiste!");
        }

        if(studentRepository.existsByCf(request.cf())){
            throw new DuplicateResourceException("Uno studente con questo CF già esiste!");
        }

        Student newStudent = studentMapper.toEntity(request);

        if(newStudent.getLivello() == null){
            newStudent.setLivello(Livello.PRINCIPIANTE);
        }

        studentRepository.save(newStudent);

        return studentMapper.toResponse(newStudent);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentByMatricola(String matricola) {
        var studente = studentRepository.findByMatricola(matricola);
        if(studente.isPresent()){
            return studentMapper.toResponse(studente.get());
        } else {
            throw new ResourceNotFoundException("Studente with this matricola not found: " + matricola);
        }
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentMapper.toStudentResponses(studentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByLivello(Livello livello) {
        return studentMapper.toStudentResponses(studentRepository.findByLivello(livello));
    }

    @Transactional
    public StudentResponse updateStudent(String matricola, StudentRequest request) {
        var studente = studentRepository.findByMatricola(matricola)
                .orElseThrow(() -> new ResourceNotFoundException("Studente with this matricola not found: " + matricola));

        studente.setNome(request.nome());
        studente.setCognome(request.cognome());
        studente.setTelefono(request.telefono());
        studente.setLivello(request.livello());

        return studentMapper.toResponse(studente);
    }

    public void deleteStudent(String matricola) {
        var studente = studentRepository.findByMatricola(matricola)
                .orElseThrow(() -> new ResourceNotFoundException("Studente with this matricola not found: " + matricola));
        studentRepository.delete(studente);
    }

    @Transactional(readOnly = true)
    public StudentReportResponse getStudentReport(String matricola) {
        var studente = studentRepository.findByMatricola(matricola)
                .orElseThrow(() -> new ResourceNotFoundException("Studente with this matricola not found: " + matricola));

        var enrollments = enrollmentRepository.findByStudentId(studente.getId());

        var nomeCompleto = studente.getNome() + " " + studente.getCognome();
        var numCorsi = enrollments.size();
        var mediaVoti = enrollments.stream()
                .filter(e -> e.getVotoFinale() != null)
                .mapToInt(e -> e.getVotoFinale())
                .average()
                .orElse(0.0);
        var corsi = enrollments.stream()
                .map(e -> e.getCourse().getNome())
                .toList();

        return new StudentReportResponse(nomeCompleto, studente.getLivello(), numCorsi, mediaVoti, corsi);
    }
}
