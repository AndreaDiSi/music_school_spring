package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.mapper.CourseMapper;
import com.scuoladimusica.mapper.LessonMapper;
import com.scuoladimusica.model.dto.request.CourseRequest;
import com.scuoladimusica.model.dto.request.LessonRequest;
import com.scuoladimusica.model.dto.response.CourseResponse;
import com.scuoladimusica.model.dto.response.LessonResponse;
import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.repository.CourseRepository;
import com.scuoladimusica.repository.InstrumentRepository;
import com.scuoladimusica.repository.LessonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class CourseService {

    private static final String CORSO_NOT_FOUND = "Course not Found";

    private CourseRepository courseRepository;

    private LessonRepository lessonRepository;

    private InstrumentRepository instrumentRepository;

    private CourseMapper courseMapper;

    private LessonMapper lessonMapper;

    @Autowired
    public CourseService(CourseRepository courseRepository, LessonRepository lessonRepository,
            InstrumentRepository instrumentRepository, CourseMapper courseMapper, LessonMapper lessonMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.instrumentRepository = instrumentRepository;
        this.courseMapper = courseMapper;
        this.lessonMapper = lessonMapper;
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {

        if (courseRepository.existsByCodiceCorso(request.codiceCorso())) {
            throw new DuplicateResourceException("Un corso con questo codice già esiste!");
        }

        if (request.dataFine().isBefore(request.dataInizio()) || request.dataFine().equals(request.dataInizio())) {
            throw new BusinessRuleException("Data inizio viene dopo la data di fine!");
        }

        Course newCourse = courseMapper.toEntity(request);
        courseRepository.save(newCourse);
        return courseMapper.toResponse(newCourse);
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseByCode(String codiceCorso) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("Corso with this Codice Corso Not found: " + codiceCorso));
        return courseMapper.toResponse(corso);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toCourseResponses(courseRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getOnlineCourses() {
        return courseMapper.toCourseResponses(courseRepository.findByOnlineTrue());
    }

    // se metti @Transactional, non serve il save alla fine perché ci pensa il persistence context
    @Transactional
    public CourseResponse updateCourse(String codiceCorso, CourseRequest request) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException(CORSO_NOT_FOUND));

        corso.setNome(request.nome());
        corso.setDataInizio(request.dataInizio());
        corso.setDataFine(request.dataFine());
        corso.setCostoOrario(request.costoOrario());
        corso.setTotaleOre(request.totaleOre());
        corso.setOnline(request.online());
        corso.setLivello(request.livello());

        if (corso.getDataFine().isBefore(corso.getDataInizio())) {
            throw new BusinessRuleException("Data fine non può venire prima di data inzio");
        }

        return courseMapper.toResponse(corso);
    }

    public void deleteCourse(String codiceCorso) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException(CORSO_NOT_FOUND));
        courseRepository.delete(corso);
    }

    @Transactional
    public LessonResponse addLesson(String codiceCorso, LessonRequest request) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException(CORSO_NOT_FOUND));
        var lesson = lessonMapper.toEntity(request);
        lesson.setCourse(corso);

        // prima controlli
        if (lessonRepository.existsByCourseIdAndNumero(corso.getId(), request.numero())) {
            throw new DuplicateResourceException("Lesson with this course id already exists");
        }

        // poi salvi
        var savedLesson = lessonRepository.save(lesson);
        corso.getLessons().add(savedLesson);

        return lessonMapper.toResponse(savedLesson);
    }

    @Transactional
    public void addInstrumentToCourse(String codiceCorso, String codiceStrumento) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso)
                .orElseThrow(() -> new ResourceNotFoundException("corso not found"));
        var strumento = instrumentRepository.findByCodiceStrumento(codiceStrumento)
                .orElseThrow(() -> new ResourceNotFoundException("instrument not found"));

        if (!corso.getInstruments().contains(strumento)) {
            corso.getInstruments().add(strumento);
        } else {
            throw new DuplicateResourceException("Strumento already in course instruments");
        }
    }
}
