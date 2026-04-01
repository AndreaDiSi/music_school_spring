package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStudentId(Long studentId);

    List<Loan> findByInstrumentId(Long instrumentId);

    Optional<Loan> findByInstrumentIdAndDataFineIsNull(Long instrumentId);

    boolean existsByInstrumentIdAndDataFineIsNull(Long instrumentId);

}
