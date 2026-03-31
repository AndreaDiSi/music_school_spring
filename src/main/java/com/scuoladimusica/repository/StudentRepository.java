package com.scuoladimusica.repository;

import java.util.List;
import java.util.Optional;

import com.scuoladimusica.model.entity.Livello;
import com.scuoladimusica.model.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByMatricola(String matricola);

    boolean existsByMatricola(String matricola);

    boolean existsByCf(String cf);

    List<Student> findByLivello(Livello livello);

}
