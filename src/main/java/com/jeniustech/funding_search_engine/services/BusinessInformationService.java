package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.BusinessInformationDTO;
import com.jeniustech.funding_search_engine.entities.Address;
import com.jeniustech.funding_search_engine.entities.BusinessInformation;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.mappers.BusinessInformationMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.BusinessInformationRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessInformationService {

    private final BusinessInformationRepository businessInformationRepository;
    private final UserDataRepository userDataRepository;

    public BusinessInformationDTO getBusinessInformation(JwtModel jwtModel) {
        UserData userData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessInformation businessInformation = userData.getBusinessInformation();

        return BusinessInformationMapper.map(
                businessInformation
        );
    }

    public BusinessInformationDTO saveBusinessInformation(JwtModel jwtModel, BusinessInformationDTO businessInformationDTO) {
        UserData userData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BusinessInformation businessInformation = userData.getBusinessInformation();
        if (businessInformation == null) {
            businessInformation = BusinessInformation.builder()
                    .user(userData)
                    .build();
            userData.setBusinessInformation(businessInformation);
        }

        if (StringUtil.isNotEmpty(businessInformationDTO.getName())) {
            businessInformation.setName(businessInformationDTO.getName());
        }
        if (StringUtil.isNotEmpty(businessInformationDTO.getVatNumber())) {
            businessInformation.setVatNumber(businessInformationDTO.getVatNumber());
        }

        businessInformation.setEmail(StringUtil.valueOrDefault(businessInformationDTO.getEmail(), null));
        businessInformation.setPhoneNumber(StringUtil.valueOrDefault(businessInformationDTO.getPhoneNumber(), null));

        if (businessInformationDTO.getAddress() != null) {
            if (businessInformation.getAddress() == null) {
                businessInformation.setAddress(Address.builder().build());
            }
            businessInformation.getAddress().setStreet(StringUtil.valueOrDefault(businessInformationDTO.getAddress().getStreet(), null));
            businessInformation.getAddress().setCity(StringUtil.valueOrDefault(businessInformationDTO.getAddress().getCity(), null));
            businessInformation.getAddress().setPostCode(StringUtil.valueOrDefault(businessInformationDTO.getAddress().getPostCode(), null));
            businessInformation.getAddress().setCountry(businessInformationDTO.getAddress().getCountry());
        }

        return BusinessInformationMapper.map(businessInformationRepository.save(businessInformation));
    }
}
