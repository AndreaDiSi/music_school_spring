package com.scuoladimusica.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scuoladimusica.model.dto.request.InstrumentRequest;
import com.scuoladimusica.model.dto.response.InstrumentResponse;
import com.scuoladimusica.model.entity.Instrument;

@Component
public class InstrumentMapper {

    public InstrumentResponse toResponse(Instrument instrument) {
        return new InstrumentResponse(
                instrument.getId(),
                instrument.getCodiceStrumento(),
                instrument.getNome(),
                instrument.getTipoStrumento(),
                instrument.getMarca(),
                instrument.getAnnoProduzione(),
                instrument.isDisponibile()
        );
    }

    public Instrument toEntity(InstrumentRequest request) {
        return Instrument.builder()
                .codiceStrumento(request.codiceStrumento())
                .nome(request.nome())
                .tipoStrumento(request.tipoStrumento())
                .marca(request.marca())
                .annoProduzione(request.annoProduzione())
                .numeroCorde(request.numeroCorde())
                .tipoCorde(request.tipoCorde())
                .materiale(request.materiale())
                .tipoPelle(request.tipoPelle())
                .diametro(request.diametro())
                .build();
    }

    public List<InstrumentResponse> toInstrumentResponses(List<Instrument> instruments) {
        List<InstrumentResponse> instrumentResponses = new ArrayList<>();
        for (Instrument instrument : instruments) {
            instrumentResponses.add(toResponse(instrument));
        }
        return instrumentResponses;
    }
}
