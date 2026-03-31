package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseId(Long courseId);

    boolean existsByCourseIdAndNumero(Long courseId, int numero);

}
