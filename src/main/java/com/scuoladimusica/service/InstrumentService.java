package com.scuoladimusica.service;

import com.scuoladimusica.exception.BusinessRuleException;
import com.scuoladimusica.exception.DuplicateResourceException;
import com.scuoladimusica.exception.ResourceNotFoundException;
import com.scuoladimusica.mapper.InstrumentMapper;
import com.scuoladimusica.mapper.LoanMapper;
import com.scuoladimusica.model.dto.request.InstrumentRequest;
import com.scuoladimusica.model.dto.response.InstrumentResponse;
import com.scuoladimusica.model.dto.response.LoanResponse;
import com.scuoladimusica.model.entity.Instrument;
import com.scuoladimusica.model.entity.Loan;
import com.scuoladimusica.repository.InstrumentRepository;
import com.scuoladimusica.repository.LoanRepository;
import com.scuoladimusica.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InstrumentService {

    //@Autowired
    private InstrumentRepository instrumentRepository;

    //@Autowired
    private LoanRepository loanRepository;

    //@Autowired
    private StudentRepository studentRepository;

    @Autowired
    public InstrumentService(InstrumentRepository instrumentRepository, LoanRepository loanRepository,
            StudentRepository studentRepository) {
        this.instrumentRepository = instrumentRepository;
        this.loanRepository = loanRepository;
        this.studentRepository = studentRepository;
    }

    private InstrumentMapper instrumentMapper = new InstrumentMapper();
    private LoanMapper loanMapper = new LoanMapper();

    public InstrumentResponse createInstrument(InstrumentRequest request) {

        if(instrumentRepository.existsByCodiceStrumento(request.codiceStrumento())){
            throw new DuplicateResourceException("Uno strumento con questo codice già esiste!");
        }

        Instrument newInstrument = instrumentMapper.toEntity(request);
        instrumentRepository.save(newInstrument);
        return instrumentMapper.toResponse(newInstrument);
    }

    @Transactional(readOnly = true)
    public InstrumentResponse getInstrumentByCode(String codiceStrumento) {
        var strumento = instrumentRepository.findByCodiceStrumento(codiceStrumento);
        if(strumento.isPresent()){
            var s = strumento.get();
            
            return instrumentMapper.toResponse(s);
            
           
        } else {
            throw new ResourceNotFoundException("Strumento with this codice not found: " + codiceStrumento);
        }
    }

    @Transactional(readOnly = true)
    public List<InstrumentResponse> getAllInstruments() {
        return instrumentMapper.toInstrumentResponses(instrumentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<InstrumentResponse> getAvailableInstruments() {
        return instrumentMapper.toInstrumentResponses(instrumentRepository.availableInstruments());
    }

    @Transactional
    public LoanResponse returnInstrument(String codiceStrumento, LocalDate dataFine) {
        var strumento = instrumentRepository.findByCodiceStrumento(codiceStrumento)
                .orElseThrow(() -> new ResourceNotFoundException("Strumento not found: " + codiceStrumento));

        var activeLoan = loanRepository.findByInstrumentIdAndDataFineIsNull(strumento.getId())
                .orElseThrow(() -> new BusinessRuleException("Lo strumento non è attualmente in prestito"));

        if (dataFine.isBefore(activeLoan.getDataInizio())) {
            throw new BusinessRuleException("La data di restituzione non può essere precedente alla data di inizio prestito");
        }

        activeLoan.setDataFine(dataFine);
        var savedLoan = loanRepository.save(activeLoan);

        return loanMapper.toResponse(savedLoan);
    }

    @Transactional
    public LoanResponse loanToStudent(String codiceStrumento, String matricolaStudente, LocalDate dataInizio) {

        if(instrumentRepository.existsByCodiceStrumento(codiceStrumento)){
            if(studentRepository.existsByMatricola(matricolaStudente)){
                var strumento = instrumentRepository.findByCodiceStrumento(codiceStrumento).get();
                var studente = studentRepository.findByMatricola(matricolaStudente).get();

                if(!strumento.isDisponibile()){
                    throw new BusinessRuleException("Lo strumento è già in prestito");
                }

                var loan = Loan.builder()
                        .instrument(strumento)
                        .student(studente)
                        .dataInizio(dataInizio)
                        .dataFine(null)
                        .build();

                
                
                     
                var savedLoan = loanRepository.save(loan);
                strumento.getLoans().add(savedLoan);  // isDisponibile() lo calcola da qui    
                 
                
                return loanMapper.toResponse(savedLoan);

            } else {
                throw new ResourceNotFoundException("studente not found");
            }
        } else {
            throw new ResourceNotFoundException("strumento not found");
        }
    }

}
