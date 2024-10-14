package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.LongTextDTO;
import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.UserCallJoin;

import java.util.Comparator;
import java.util.List;

public interface CallMapper {

    static List<CallDTO> mapJoin(List<UserCallJoin> call, boolean isSearch, boolean isFavorite, boolean withLongText) {
        if (call == null) {
            return null;
        }
        return call.stream().map(c -> mapJoin(c, isSearch, isFavorite, withLongText)).toList();
    }
    static CallDTO mapJoin(UserCallJoin call, boolean isSearch, boolean isFavorite, boolean withLongText) {
        if (call == null) {
            return null;
        }
        return map(call.getCallData(), isSearch, isFavorite, withLongText);
    }
    static CallDTO map(Call call, boolean isSearch, boolean isFavorite, boolean withLongText) {
        if (call == null) {
            return null;
        }
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .longTexts(!withLongText ? null : map(call.getLongTexts()))
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

    static List<LongTextDTO> map(List<LongText> longTexts) {
        return longTexts.stream().map(CallMapper::map).toList();
    }

    static LongTextDTO map(LongText longText) {
        return LongTextDTO.builder()
                .type(longText.getType())
                .text(longText.getText())
                .build();
    }

    static CallDTO mapToGraphMesh(Call call) {
        if (call == null) {
            return null;
        }
        CallDTO callDTO = mapToGraphMeshChild(call);
        callDTO.setProjects(ProjectMapper.mapToGraphMeshChild(call.getProjects()));
        return callDTO;
    }
    static CallDTO mapToGraphMeshChild(Call call) {
        if (call == null) {
            return null;
        }
        return CallDTO.builder()
                .id(call.getId())
                .identifier(call.getIdentifier())
                .title(call.getTitle())
                .endDate(DateMapper.map(call.getEndDate()))
                .endDate2(DateMapper.map(call.getEndDate2()))
                .startDate(DateMapper.map(call.getStartDate()))
                .budgetMin(call.getBudgetMinDisplayString())
                .budgetMax(call.getBudgetMaxDisplayString())
                .projectNumber(call.getProjectNumber())
                .build();
    }

    static void sortByEndDate(List<Call> calls) {
        if (calls == null) {
            return;
        }
        calls.sort(Comparator.comparing(Call::getEndDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
    }
}
