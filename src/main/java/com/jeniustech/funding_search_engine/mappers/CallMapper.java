package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;

import java.util.stream.Collectors;

public interface CallMapper {

    static CallDTO map(Call call) {
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .longTexts(call.getLongTexts().stream()
                        .collect(Collectors.toMap(LongText::getType, LongText::getText)))
                .actionType(call.getActionType())
                .endDate(DateMapper.map(call.getEndDate()))
                .endDate2(DateMapper.map(call.getEndDate2()))
                .startDate(DateMapper.map(call.getStartDate()))
                .budgetMin(call.getBudgetMin().toString())
                .budgetMax(call.getBudgetMax().toString())
                .projectNumber(call.getProjectNumber())
                .urlId(call.getUrlId())
                .urlType(call.getUrlType())
                .typeOfMGADescription(call.getTypeOfMGADescription())
                .build();
    }

}
