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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final UserCallJoinRepository userCallJoinRepository;
    private final UserDataRepository userDataRepository;

    public CallDTO getCallDTOById(Long id) {
        return CallMapper.map(getCallById(id));
    }

    private Call getCallById(Long id) {
        return callRepository.findById(id).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    public void favoriteCall(Long id, String userId) {
        UserData userData = userDataRepository.findBySubjectId(userId).orElseThrow(() -> new CallNotFoundException("User not found"));
        Call call = getCallById(id);
        UserCallJoin userCallJoin = UserCallJoin.builder()
                .userData(userData)
                .callData(call)
                .type(UserCallJoinTypeEnum.FAVORITE)
                .build();
    }

    public void unFavoriteCall(Long id, String userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
