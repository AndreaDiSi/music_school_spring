package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Enrollment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    public List<Enrollment> findByStudentId(Long studentId);

    public List<Enrollment> findByCourseId(Long courseId);

    public boolean existsByStudentMatricolaAndCourseCodiceCorso(String matricola, String codiceCorso);

    public Optional<Enrollment> findByStudentMatricolaAndCourseCodiceCorso(String matricola, String codiceCorso);
}
