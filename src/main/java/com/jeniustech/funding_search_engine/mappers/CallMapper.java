package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;

public class CallMapper {

    public static CallDTO map(Call call) {
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .description(call.getDescription())
                .displayDescription(call.getDisplayDescription())
                .actionType(call.getActionType())
                .submissionDeadlineDate(call.getSubmissionDeadlineDate())
                .openDate(call.getOpenDate())
                .budget(call.getBudget())
                .projectNumber(call.getProjectNumber())
                .build();
    }

}
