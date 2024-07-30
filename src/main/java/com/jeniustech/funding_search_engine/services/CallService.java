package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.mappers.CallMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.UserCallJoinRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CallService extends IDataService<CallDTO> {

    private final CallRepository callRepository;
    private final UserCallJoinRepository userCallJoinRepository;

    public CallService(UserDataRepository userDataRepository, CallRepository callRepository, UserCallJoinRepository userCallJoinRepository) {
        super(userDataRepository);
        this.callRepository = callRepository;
        this.userCallJoinRepository = userCallJoinRepository;
    }

    public CallDTO getDTOById(Long id) {
        return CallMapper.map(getById(id), false, false, true);
    }

    private Call getById(Long callId) {
        return callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    public boolean isFavorite(Long callId, Long userId) {
        return userCallJoinRepository.findByReferenceIdAndUserIdAndType(callId, userId, UserCallJoinTypeEnum.FAVORITE).isPresent();
    }

    public void favorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userCallJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE));

        Call call = getById(id);
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

    public void unFavorite(Long callId, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        Call call = getById(callId);
        Optional<UserCallJoin> userCallJoin = userCallJoinRepository.findByReferenceIdAndUserIdAndType(call.getId(), userData.getId(), UserCallJoinTypeEnum.FAVORITE);
        if (userCallJoin.isEmpty()) {
            return;
        }
        userCallJoinRepository.delete(userCallJoin.get());
    }

    public SearchDTO<CallDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getId).descending());

        List<Long> ids = userCallJoinRepository.findByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE, pageable);
        List<CallDTO> results = callRepository.findAllById(ids).stream()
                .map(call -> CallMapper.map(call, true, true, false))
                .toList();

        return SearchDTO.<CallDTO>builder()
                .results(results)
                .totalResults(userCallJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE))
                .build();
    }

    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userCallJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserCallJoinTypeEnum.FAVORITE);
    }
}
