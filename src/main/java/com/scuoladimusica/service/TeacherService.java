package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.mapper.TeacherMapper;
import com.scuoladimusica.model.dto.request.TeacherRequest;
import com.scuoladimusica.model.dto.response.TeacherResponse;
import com.scuoladimusica.model.entity.Teacher;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeacherService {

    private static final String INSEGNANTE_NOT_FOUND = "Insegnante with this matricola not found: ";

    private TeacherRepository teacherRepository;

    private CourseRepository courseRepository;

    private TeacherMapper teacherMapper = new TeacherMapper();

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    public TeacherResponse createTeacher(TeacherRequest request) {

        if(teacherRepository.existsByMatricolaInsegnante(request.matricolaInsegnante())){
            throw new DuplicateResourceException("Un insegnante con questa matricola già esiste!");
        }

        if(teacherRepository.existsByCf(request.cf())){
            throw new DuplicateResourceException("Un insegnante con questo CF già esiste!");
        }

        if(request.stipendio() <= 0){
            throw new BusinessRuleException("Lo stipendio deve essere maggiore di 0!");
        }

        Teacher newTeacher = teacherMapper.toEntity(request);
        teacherRepository.save(newTeacher);
        return teacherMapper.toResponse(newTeacher);
    }

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByMatricola(String matricola) {
        var insegnante = teacherRepository.findByMatricolaInsegnante(matricola)
                .orElseThrow(() -> new ResourceNotFoundException(INSEGNANTE_NOT_FOUND + matricola));
        return teacherMapper.toResponse(insegnante);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {
        return teacherMapper.toTeacherResponses(teacherRepository.findAll());
    }

    @Transactional
    public TeacherResponse updateTeacher(String matricola, TeacherRequest request) {
        var insegnante = teacherRepository.findByMatricolaInsegnante(matricola)
                .orElseThrow(() -> new ResourceNotFoundException(INSEGNANTE_NOT_FOUND + matricola));

        insegnante.setNome(request.nome());
        insegnante.setCognome(request.cognome());
        insegnante.setTelefono(request.telefono());
        insegnante.setStipendio(request.stipendio());
        insegnante.setSpecializzazione(request.specializzazione());
        insegnante.setAnniEsperienza(request.anniEsperienza());

        return teacherMapper.toResponse(insegnante);
    }

    public void deleteTeacher(String matricola) {
        var insegnante = teacherRepository.findByMatricolaInsegnante(matricola)
                .orElseThrow(() -> new ResourceNotFoundException(INSEGNANTE_NOT_FOUND + matricola));
        teacherRepository.delete(insegnante);
    }

    @Transactional
    public void assignCourse(String matricolaInsegnante, String codiceCorso) {

        var insegnante = teacherRepository.findByMatricolaInsegnante(matricolaInsegnante)
                .orElseThrow(() -> new ResourceNotFoundException("insegnante not found"));
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("corso not found"));

        if (corso.getTeacher() != null) {
            throw new BusinessRuleException("Il corso è già assegnato a un insegnante");
        }

        corso.setTeacher(insegnante);
        insegnante.getCourses().add(corso);
    }
}
