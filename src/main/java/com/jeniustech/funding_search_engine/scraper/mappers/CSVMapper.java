package com.jeniustech.funding_search_engine.scraper.mappers;

import com.jeniustech.funding_search_engine.scraper.models.CallCSVDetails;
import com.jeniustech.funding_search_engine.scraper.models.EUCallDTO;
import com.jeniustech.funding_search_engine.scraper.models.EUCallDetailDTO;

import static com.jeniustech.funding_search_engine.constants.ScraperConstants.singleStage;

public interface CSVMapper {

    static CallCSVDetails map(
            EUCallDTO item,
            EUCallDetailDTO callDetailDTO
    ) {
        String submissionProcedure = item.getDeadlineModel() != null ? item.getDeadlineModel() : callDetailDTO.getSubmissionProcedure();

        return CallCSVDetails.builder()
                .identifier(item.getIdentifier())
                .title(callDetailDTO.getTitle(item.getTitle()))
                .description(callDetailDTO.getDescription())
                .missionDetails(callDetailDTO.getMissionDetails())
                .destinationDetails(callDetailDTO.getDestinationDetails())
                .actionType(callDetailDTO.getActionType())

                .deadline(item.getDeadlineDate(submissionProcedure))
                .deadline2(item.getDeadlineDate2(submissionProcedure))

                .startDate(item.getStartDate())
                .budgetMin(callDetailDTO.getMinBudget())
                .budgetMax(callDetailDTO.getMaxBudget())
                .projectNumber(callDetailDTO.getNumberOfGrants())
                .pathId(item.getCCM2Id())
                .reference(item.getReference())

                .typeOfMGADescription(callDetailDTO.getTypeOfMGADescription())
                .submissionProcedure(submissionProcedure)

                .beneficiaryAdministration(callDetailDTO.getBeneficiaryAdministration())
                .furtherInformation(callDetailDTO.getFurtherInformation())
                .build();
    }

    static CallCSVDetails map(
            EUCallDTO item
    ) {
        return CallCSVDetails.builder()
                .identifier(item.getIdentifier())
                .title(item.getTitle())
                .description(item.getDescription())
                .actionType(item.getTypeOfAction())
                .deadline(item.getDeadlineDate(singleStage))
                .startDate(item.getStartDate())
                .budgetMin(item.getBudget())
                .budgetMax(item.getBudget())
                .submissionProcedure(item.getDeadlineModel())
                .projectNumber(null)
                .pathId(item.getCCM2Id())
                .reference(item.getReference())
                .beneficiaryAdministration(item.getBeneficiaryAdministration())
                .furtherInformation(item.getFurtherInformation())
                .duration(item.getDuration())
                .build();
    }

}
