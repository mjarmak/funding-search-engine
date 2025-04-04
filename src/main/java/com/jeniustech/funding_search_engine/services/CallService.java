package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
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

    public CallDTO getDTOById(Long id, String subjectId) {
        return getDTOById(id, subjectId, false);
    }
    public CallDTO getDTOById(Long id, String subjectId, boolean hasSecretAccess) {
        UserData userData = getUserOrNotFound(subjectId);
        return CallMapper.map(getById(id, hasSecretAccess), false, isFavorite(id, userData.getId()), true);
    }

    private Call getById(Long callId, boolean hasSecretAccess) {
        return callRepository.findById(callId, hasSecretAccess).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    public boolean isFavorite(Long callId, Long userId) {
        return userCallJoinRepository.findByReferenceIdAndUserIdAndType(callId, userId, UserJoinTypeEnum.FAVORITE).isPresent();
    }

    public void favorite(Long id, String subjectId) {
        favorite(id, subjectId, false);
    }
    public void favorite(Long id, String subjectId, boolean hasSecretAccess) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userCallJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE));

        Call call = getById(id, hasSecretAccess);
        if (isFavorite(call.getId(), userData.getId())) {
            return;
        }
        UserCallJoin userCallJoin = UserCallJoin.builder()
                .userData(userData)
                .callData(call)
                .type(UserJoinTypeEnum.FAVORITE)
                .build();
        userCallJoinRepository.save(userCallJoin);
    }

    public void unFavorite(Long callId, String subjectId) {
        unFavorite(callId, subjectId, false);
    }
    public void unFavorite(Long callId, String subjectId, boolean hasSecretAccess) {
        UserData userData = getUserOrNotFound(subjectId);
        Call call = getById(callId, hasSecretAccess);
        Optional<UserCallJoin> userCallJoin = userCallJoinRepository.findByReferenceIdAndUserIdAndType(call.getId(), userData.getId(), UserJoinTypeEnum.FAVORITE);
        if (userCallJoin.isEmpty()) {
            return;
        }
        userCallJoinRepository.delete(userCallJoin.get());
    }

    public SearchDTO<CallDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getId).descending());

        List<UserCallJoin> joins = userCallJoinRepository.findByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE, pageable);
        List<CallDTO> results = CallMapper.mapJoin(joins, true, true, false);

        return SearchDTO.<CallDTO>builder()
                .results(results)
                .totalResults(userCallJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE))
                .build();
    }

    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userCallJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserJoinTypeEnum.FAVORITE);
    }

    public CallDTO getGraphMesh(Long id) {
        return CallMapper.mapToGraphMesh(getById(id, false));
    }
}
