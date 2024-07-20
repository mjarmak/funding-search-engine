package com.jeniustech.funding_search_engine.scraper.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeniustech.funding_search_engine.exceptions.FieldNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.MapperException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import static com.jeniustech.funding_search_engine.scraper.Constants.multipleCutoff;
import static com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil.getLocalDateTime;

@Slf4j
@Value
@Builder
public class EUCallDTO {
    // Hi-tech capacities for crisis response and recovery after a natural-technological (NaTech) disaster
    @JsonProperty("content")
    String title;

    public String getTitle() {
        if (metadata.getTitle() != null) {
            return metadata.getTitle();
        } else if (title != null) {
            return title;
        } else if (metadata.getProjectName() != null) {
            return metadata.getProjectName();
        } else {
            throw new FieldNotFoundException("No title found");
        }
    }

    CallMetaDataDTO metadata;

    // 46905556EDFDevelopmentActions1718841600000
    String reference;

    // https://ec.europa.eu/info/funding-tenders/opportunities/data/topicDetails/EDF-2024-DA-NAVAL-FNP.json
    @JsonProperty("url")
    String jsonUrl;

    public String toString() {
        return "CallDTO(identifier=" + getIdentifier() + ")";
    }

    public String getIdentifier() {
        return metadata.getIdentifier();
    }

    public String getBudget() {
        return metadata.getBudget();
    }

    public String getStartDate() {
        return metadata.getStartDate();
    }

    public String getDeadlineDate(String submissionProcedure) {
        if (submissionProcedure.equals(multipleCutoff)) {
            if (metadata.deadlineDate == null || metadata.deadlineDate.isEmpty()) {
                throw new MapperException("No deadline date found");
            }
            List<String> futureDates = metadata.deadlineDate.stream()
                    .filter(date -> date != null && getLocalDateTime(date).isAfter(LocalDateTime.now()))
                    .toList();
            if (futureDates.isEmpty()) {
                if (metadata.deadlineDate.size() >= 2) {
                    return metadata.deadlineDate.get(metadata.deadlineDate.size() - 1);
                } else if (metadata.deadlineDate.size() == 1) {
                    return metadata.deadlineDate.get(0);
                } else {
                    return null;
                }
            } else {
                return futureDates.get(0);
            }
        } else {
            if (metadata.getDeadlineDate() != null) {
                return metadata.getDeadlineDate();
            } else if (metadata.getUpdateDate() != null) {
                return metadata.getUpdateDate();
            } else {
                throw new MapperException("No deadline date found");
            }
        }
    }

    public String getDeadlineDate2(String submissionProcedure) {
        if (submissionProcedure.equals(multipleCutoff)) {
            if (metadata.deadlineDate == null || metadata.deadlineDate.isEmpty()) {
                throw new MapperException("No deadline date found");
            }
            List<String> futureDates = metadata.deadlineDate.stream()
                    .filter(date -> date != null && getLocalDateTime(date).isAfter(LocalDateTime.now()))
                    .toList();
            if (futureDates.isEmpty()) {
                return null;
            } else if (futureDates.size() >= 2) {
                return futureDates.get(1);
            } else {
                return null;
            }
        } else {
            return metadata.getDeadlineDate2();
        }
    }

    public String getTypeOfAction() {
        return metadata.getTypeOfAction();
    }

    public String getCCM2Id() {
        return getFirst(metadata.getCallccm2Id(), false);
    }

    public String getDescription() {
        if (metadata.getDescription() != null) {
            return metadata.getDescription();
        } else {
            return null;
        }
    }


    public static String getFirst(List<String> list, boolean throwException) {
        if (list == null || list.isEmpty() || list.get(0) == null) {
            if (throwException) {
                throw new FieldNotFoundException("list is empty");
            }
            return null;
        }
        return list.get(0);
    }

    public static String getSecond(List<String> list) {
        if (list == null || list.size() < 2 || list.get(1) == null) {
            return null;
        }
        return list.get(1);
    }

    public boolean hasJsonUrl() {
        return this.jsonUrl.contains(".json");
    }

    public String getJsonUrl() {
        // lower case last part of url
        return jsonUrl.substring(0, jsonUrl.lastIndexOf("/") + 1) + getIdentifier().toLowerCase() + ".json";
    }

    public String getBeneficiaryAdministration() {
        return metadata.getBeneficiaryAdministration();
    }

    public String getFurtherInformation() {
        return metadata.getFurtherInformation();
    }

    public String getDuration() {
        return metadata.getDuration();
    }

    @Value
    @Builder
    public static class CallMetaDataDTO {

        List<String> budget;
        List<String> identifier;
        List<String> title;
        List<String> projectName;

        List<String> callccm2Id;

        List<String> furtherInformation;
        List<String> beneficiaryAdministration;
        List<String> description;
        List<String> duration;

        List<String> startDate;
        List<String> deadlineDate;
        List<String> updateDate;

        // HORIZON Research and Innovation Actions
        List<String> typesOfAction;

        public String getTitle() {
            return getFirst(title, false);
        }

        public String getProjectName() {
            return getFirst(projectName, false);
        }

        public String getBudget() {
            return getFirst(budget, true);
        }

        public String getIdentifier() {
            return getFirst(identifier, false);
        }

        public String getStartDate() {
            return getFirst(startDate, true);
        }

        public String getDeadlineDate() {
            return getFirst(deadlineDate, false);
        }

        public String getDeadlineDate2() {
            return getSecond(deadlineDate);
        }

        public String getUpdateDate() {
            return getFirst(updateDate, false);
        }

        public String getTypeOfAction() {
            return getFirst(typesOfAction, false);
        }

        public String getFurtherInformation() {
            return getFirst(furtherInformation, false);
        }

        public String getBeneficiaryAdministration() {
            return getFirst(beneficiaryAdministration, false);
        }

        public String getDescription() {
            return getFirst(description, false);
        }

        public String getDuration() {
            return getFirst(duration, false);
        }
    }


}
