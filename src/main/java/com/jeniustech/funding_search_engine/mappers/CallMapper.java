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
                .destinationDetails(call.getDestinationDetails())
                .missionDetails(call.getMissionDetails())
                .actionType(call.getActionType())
                .submissionDeadlineDate(DateMapper.map(call.getSubmissionDeadlineDate()))
                .submissionDeadlineDate2(DateMapper.map(call.getSubmissionDeadlineDate2()))
                .openDate(DateMapper.map(call.getOpenDate()))
                .budget(call.getBudgetString())
                .projectNumber(call.getProjectNumber())
                .pathId(call.getPathId())
                .reference(call.getReference())
                .typeOfMGA(call.getTypeOfMGA())
                .typeOfMGADescription(call.getTypeOfMGADescription())
                .build();
    }

}
