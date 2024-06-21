package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final UserCallJoinRepository userCallJoinRepository;
    private final UserDataRepository userDataRepository;

    public CallDTO getCallDTOById(Long id) {
        return CallMapper.map(getCallById(id), false, false);
    }

    private Call getCallById(Long callId) {
        return callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    private boolean isFavorite(Long callId, Long userId) {
        return userCallJoinRepository.findFavoriteByCallAndUserId(callId, userId).isPresent();
    }

    public void favoriteCall(Long callId, String subjectId) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new CallNotFoundException("User not found"));

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userCallJoinRepository.countFavoritesByUserId(userData.getId()));

        Call call = getCallById(callId);
        if (isFavorite(call.getId(), userData.getId())) {
            return;
        }
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
        Optional<UserCallJoin> userCallJoin = userCallJoinRepository.findFavoriteByCallAndUserId(call.getId(), userData.getId());
        if (userCallJoin.isEmpty()) {
            return;
        }
        userCallJoinRepository.delete(userCallJoin.get());
    }

    public SearchDTO<CallDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new CallNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getId).descending());

        List<CallDTO> results = userCallJoinRepository.findFavoritesByUserId(userData.getId(), pageable).stream()
                .map(UserCallJoin::getCallData)
                .map(call -> CallMapper.map(call, true, true))
                .toList();

        return SearchDTO.<CallDTO>builder()
                .results(results)
                .totalResults(userCallJoinRepository.countFavoritesByUserId(userData.getId()))
                .build();
    }

    public List<Long> checkFavoriteCalls(UserData userData, List<Long> ids) {
        return userCallJoinRepository.findFavoriteByCallIds(userData.getId(), ids);
    }
}
