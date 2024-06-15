package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.mappers.CallMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.UserCallJoinRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final UserCallJoinRepository userCallJoinRepository;
    private final UserDataRepository userDataRepository;

    public CallDTO getCallDTOById(Long id) {
        return CallMapper.map(getCallById(id), true);
    }

    private Call getCallById(Long callId) {
        return callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    public void favoriteCall(Long callId, String subjectId) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new CallNotFoundException("User not found"));
        Call call = getCallById(callId);
        UserCallJoin userCallJoin = UserCallJoin.builder()
                .userData(userData)
                .callData(call)
                .type(UserCallJoinTypeEnum.FAVORITE)
                .build();
        userCallJoinRepository.save(userCallJoin);
    }

    public void unFavoriteCall(Long callId, String subjectId) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new CallNotFoundException("User not found"));
        Call call = getCallById(callId);
        UserCallJoin userCallJoin = userCallJoinRepository.findFavoriteByCallAndUserId(call.getId(), userData.getId())
                .orElseThrow(() -> new CallNotFoundException("Favorite not found"));
        userCallJoinRepository.delete(userCallJoin);
    }

    public List<CallDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new CallNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getCreatedAt).descending());

        return userCallJoinRepository.findFavoritesByUserId(userData.getId(), pageable).stream()
                .map(UserCallJoin::getCallData)
                .map(call -> CallMapper.map(call, false))
                .toList();
    }

}
