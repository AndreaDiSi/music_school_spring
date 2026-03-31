package com.scuoladimusica.repository;

import com.scuoladimusica.model.entity.Instrument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;


@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    public Optional<Instrument> findById(Long instrumentId);

    Optional<Instrument> findByCodiceStrumento(String codiceStrumento);

    boolean existsByCodiceStrumento(String codiceStrumento);

    @Query(nativeQuery = true,
       value = "SELECT * FROM instruments i WHERE NOT EXISTS " +
               "(SELECT 1 FROM loans l WHERE l.instrument_id = i.id AND l.data_fine IS NULL)")
    List<Instrument> availableInstruments();

}
