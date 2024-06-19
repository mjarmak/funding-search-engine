package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.LongTextDTO;
import com.jeniustech.funding_search_engine.entities.Call;

import java.util.stream.Collectors;

public interface CallMapper {

    static CallDTO map(Call call, boolean isSearch, boolean isFavorite) {
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .longTexts(isSearch ? null : call.getLongTexts().stream()
                        .map(longText -> LongTextDTO.builder()
                                .type(longText.getType())
                                .text(longText.getText())
                                .build())

                        .collect(Collectors.toList()))
                .actionType(call.getActionType())
                .endDate(DateMapper.map(call.getEndDate()))
                .endDate2(isSearch ? null : DateMapper.map(call.getEndDate2()))
                .startDate(DateMapper.map(call.getStartDate()))
                .budgetMin(call.getBudgetMin().toString())
                .budgetMax(call.getBudgetMax().toString())
                .projectNumber(call.getProjectNumber())
//                .urlId(call.getUrlId())
//                .urlType(call.getUrlType())
                .url(isSearch ? null : call.getUrl())
                .typeOfMGADescription(isSearch ? null : call.getTypeOfMGADescription())
                .favorite(isFavorite)
                .build();
    }

}
