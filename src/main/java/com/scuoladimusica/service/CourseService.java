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

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private LessonMapper lessonMapper;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {

        if(courseRepository.existsByCodiceCorso(request.codiceCorso())){
            throw new DuplicateResourceException("Un corso con questo codice già esiste!");
        }

        if(request.dataFine().isBefore(request.dataInizio()) || request.dataFine().equals(request.dataInizio())){
            throw new BusinessRuleException("Data inizio viene dopo la data di fine!");
        }
        
        Course newCourse = courseMapper.toEntity(request);

        courseRepository.save(newCourse);

        return courseMapper.toResponse(newCourse);
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseByCode(String codiceCorso) {
        var corso = courseRepository.findByCodiceCorso(codiceCorso);
        if(corso.isPresent()){
            var newCorso = courseRepository.save(corso.get());
            return courseMapper.toResponse(newCorso);
        } else {
            throw new ResourceNotFoundException("Corso with this Codice Corso Not found: " + codiceCorso);
        }
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toCourseResponses(courseRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getOnlineCourses() {
        return courseMapper.toCourseResponses(courseRepository.findByOnlineTrue());
    }

    //a quanto pare se metti transactional, non serve il save alla fine perchè ci pensa il persistance context
    @Transactional
    public CourseResponse updateCourse(String codiceCorso, CourseRequest request) {

        if(courseRepository.existsByCodiceCorso(codiceCorso)){

            var corso = courseRepository.findByCodiceCorso(codiceCorso).get();

            corso.setNome(request.nome());
            corso.setDataInizio(request.dataInizio());
            corso.setDataFine(request.dataFine());
            corso.setCostoOrario(request.costoOrario());
            corso.setTotaleOre(request.totaleOre());
            corso.setOnline(request.online());
            corso.setLivello(request.livello());

            if (corso.getDataFine().isBefore(corso.getDataInizio())){
                throw new BusinessRuleException("Data fine non può venire prima di data inzio");
            }

           return courseMapper.toResponse(corso);

        } else {
            throw new ResourceNotFoundException("Course not Found");
        }
    }

    public void deleteCourse(String codiceCorso) {
        if(courseRepository.existsByCodiceCorso(codiceCorso)){
            courseRepository.delete(courseRepository.findByCodiceCorso(codiceCorso).get());
        } else {
            throw new ResourceNotFoundException("Course not Found");
        }
        
    }

    @Transactional
    public LessonResponse addLesson(String codiceCorso, LessonRequest request) {

        if (courseRepository.existsByCodiceCorso(codiceCorso)) {
            var corso = courseRepository.findByCodiceCorso(codiceCorso).get();
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

        } else {
            throw new ResourceNotFoundException("Course not found");
        }
    }

    @Transactional
    public void addInstrumentToCourse(String codiceCorso, String codiceStrumento) {

        if(instrumentRepository.existsByCodiceStrumento(codiceStrumento)){
            if(courseRepository.existsByCodiceCorso(codiceCorso)){
                var corso = courseRepository.findByCodiceCorso(codiceCorso).get();
                var strumento = instrumentRepository.findByCodiceStrumento(codiceStrumento).get();
                var strumentiCorso = corso.getInstruments();
                if(!strumentiCorso.contains(strumento)){
                    strumentiCorso.add(strumento);
                }else{
                    throw new DuplicateResourceException("Strumento already in course instruments");
                }
            } else {
                throw new ResourceNotFoundException("corso not found");
            }
        } else {
            throw new ResourceNotFoundException("instrument not found");
        }
    }
}
