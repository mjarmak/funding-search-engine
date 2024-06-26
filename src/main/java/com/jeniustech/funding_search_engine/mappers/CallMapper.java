package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.LongTextDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.util.DetailFormatter;

import java.util.List;

public interface CallMapper {

    static CallDTO map(Call call, boolean isSearch, boolean isFavorite) {
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .longTexts(isSearch ? null : map(call.getLongTexts()))
                .actionType(call.getActionType())
                .endDate(DateMapper.map(call.getEndDate()))
                .endDate2(isSearch ? null : DateMapper.map(call.getEndDate2()))
                .startDate(DateMapper.map(call.getStartDate()))
                .budgetMin(call.getBudgetMinDisplayString())
                .budgetMax(call.getBudgetMaxDisplayString())
                .projectNumber(call.getProjectNumber())
                .url(isSearch ? null : call.getUrl())
                .typeOfMGADescription(isSearch ? null : call.getTypeOfMGADescription())
                .favorite(isFavorite)
                .build();
    }

    private static List<LongTextDTO> map(List<LongText> longTexts) {
        return longTexts.stream().map(CallMapper::map).toList();
    }

    private static LongTextDTO map(LongText longText) {
        return LongTextDTO.builder()
                .type(longText.getType())
                .text(DetailFormatter.format(longText.getText()))
                .build();
    }

}
