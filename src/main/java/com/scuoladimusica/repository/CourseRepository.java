package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Course;
import com.scuoladimusica.model.entity.Livello;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    public Optional<Course> findById(Long id);

    Optional<Course> findByCodiceCorso(String codiceCorso);

    public boolean existsByCodiceCorso(String codiceCorso);

    public boolean existsById(Long id);
   
    public List<Course> findByOnlineTrue();

    public List<Course> findByLivello(Livello livello);

    public List<Course> findByTeacherId(Long teacherId);




}
