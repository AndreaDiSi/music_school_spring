package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByMatricolaInsegnante(String matricolaInsegnante);

    boolean existsByMatricolaInsegnante(String matricolaInsegnante);

    boolean existsByCf(String cf);

}
