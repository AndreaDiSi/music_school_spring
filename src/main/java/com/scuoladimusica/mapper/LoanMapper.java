package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.response.LoanResponse;
import com.scuoladimusica.model.entity.Loan;

@Component
public class LoanMapper {


    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getInstrument().getCodiceStrumento(),
                loan.getInstrument().getNome(),
                loan.getStudent().getMatricola(),
                loan.getStudent().getNomeCompleto(),
                loan.getDataInizio(),
                loan.getDataFine()
        );
    }

    public List<LoanResponse> toLoanResponses(List<Loan> loans) {
        List<LoanResponse> loanResponses = new ArrayList<>();
        for (Loan loan : loans) {
            loanResponses.add(toResponse(loan));
        }
        return loanResponses;
    }
}
