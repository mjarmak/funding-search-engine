package com.jeniustech.funding_search_engine.scraper.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeniustech.funding_search_engine.exceptions.MapperException;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class EUCallDetailDTO {

    @JsonProperty("TopicDetails") TopicDetailsDTO topicDetails;

    @JsonIgnore
    public String getTitle(String defaultTitle) {
        if (defaultTitle != null) {
            return defaultTitle;
        } else if (topicDetails.getCallTitle() != null) {
            return topicDetails.getCallTitle();
        } else if (topicDetails.getTitle() != null) {
            return topicDetails.getTitle();
        }
        throw new MapperException("Title is not found");
    }

    @JsonIgnore
    @Nullable
    private BudgetItem getBudget() {
        String actionString = topicDetails.identifier + " - " + getActionType();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (Map.Entry<String, Object> entry : topicDetails.budgetOverview.budgetTopicActionMap.entrySet()) {
            Object budgetItemOrList = entry.getValue();
            if (budgetItemOrList instanceof List<?>) {
                for (Object o : (List<?>) budgetItemOrList) {
                    BudgetItem budgetItem = objectMapper.convertValue(o, BudgetItem.class);
                    if (budgetItem.action.contains(actionString) || actionString.contains(budgetItem.action)) {
                        return budgetItem;
                    }
                }
            } else if (budgetItemOrList instanceof LinkedHashMap<?,?>) {
                BudgetItem budgetItem = objectMapper.convertValue(budgetItemOrList, BudgetItem.class);
                if (budgetItem.action.contains(actionString) || actionString.contains(budgetItem.action)) {
                    return budgetItem;
                }
            }
        }
        return null;
//        throw new MapperException("Budget is not found");
    }

    @JsonIgnore
    public String getMinBudget() {
        if (getBudget() == null) {
            return null;
        }
        if (getBudget().minContribution != null) {
            return getBudget().minContribution;
        }
        return getLastYearBudget();

    }

    @JsonIgnore
    public String getMaxBudget() {
        if (getBudget() == null) {
            return null;
        }
        if (getBudget().maxContribution != null) {
            return getBudget().maxContribution;
        }
        return getLastYearBudget();
    }

    @JsonIgnore
    private String getLastYearBudget() {
        if (getBudget() == null) {
            return null;
        }
        return getBudget().budgetYearMap.values().stream().max(String::compareTo).orElse(null);
    }

    @JsonIgnore
    public Integer getNumberOfGrants() {
        if (getBudget() == null) {
            return null;
        }
        return getBudget().expectedGrants;
    }


    @JsonIgnore
    public String getActionType() {
        Object type = topicDetails.actions.get(0).types.get(0);
        if (type instanceof String) { // if type string, means only contains type of action
            return (String) type;
        } else if (type instanceof LinkedHashMap<?,?>) {
            return ActionType.of((LinkedHashMap<String, Object>) type).typeOfAction;
        } else if (type instanceof ActionType) {
            return ((ActionType) type).typeOfAction;
        } else {
            throw new MapperException("Action type is not found");
        }
    }


    @JsonIgnore
    public String getSubmissionProcedure() {
        if (
                topicDetails.actions.get(0).submissionProcedure == null ||
                topicDetails.actions.get(0).submissionProcedure.description == null) {
            BudgetItem budget = getBudget();
            if (getBudget() == null) {
                return null;
            }
            if (budget.getDeadlineModel() != null) {
                return budget.getDeadlineModel();
            }
            throw new MapperException("Submission procedure is not found");
        }
        return topicDetails.actions.get(0).submissionProcedure.description;
    }

    @JsonIgnore
    public String getTypeOfMGADescription() {
        Object type = topicDetails.actions.get(0).types.get(0);
        if (type instanceof String) { // if type string, means only contains type of action
            return null;
        } else if (type instanceof LinkedHashMap<?,?>) {
            List<MGATypeOrSubmissionProcedure> typeOfMGA = ActionType.of((LinkedHashMap<String, Object>) type).typeOfMGA;
            if (typeOfMGA == null || typeOfMGA.isEmpty()) {
                return null;
            }
            return typeOfMGA.get(0).description;
        } else if (type instanceof ActionType) {
            return ((ActionType) type).typeOfMGA.get(0).description;
        } else {
            throw new MapperException("Type of MGA is not found");
        }
    }



    @JsonIgnore
    public String getDescription() {
        return topicDetails.description;
    }

    @JsonIgnore
    public String getBeneficiaryAdministration() {
        return topicDetails.beneficiaryAdministration;
    }

    @JsonIgnore
    public String getFurtherInformation() {
        return topicDetails.furtherInformation;
    }

    @JsonIgnore
    public String getDestinationDetails() {
        return topicDetails.destinationDetails;
    }

    @JsonIgnore
    public String getMissionDetails() {
        return topicDetails.missionDetails;
    }


    @Getter
    public static class TopicDetailsDTO {

        @JsonProperty("identifier") String identifier;
        @JsonProperty("title") String title;
        @JsonProperty("callTitle") String callTitle;
        @JsonProperty("description") String description;
        String destinationDetails;
        String missionDetails;

        String beneficiaryAdministration;
        String furtherInformation;
        String conditions;

        @JsonProperty("actions") List<ActionItem> actions;
        @JsonProperty("budgetOverviewJSONItem") BudgetOverviewDTO budgetOverview;
    }

    @Getter
    public static class BudgetOverviewDTO {

        Map<String, Object> budgetTopicActionMap;

        @JsonCreator
        public BudgetOverviewDTO(@JsonProperty("budgetTopicActionMap") Object budgetTopicActionMap) {
            ObjectMapper mapper = new ObjectMapper();
            this.budgetTopicActionMap = mapper.convertValue(budgetTopicActionMap, Map.class);
        }

    }

    @Getter
    public static class BudgetItem {

        @JsonProperty("action") String action;
        @JsonProperty("expectedGrants") Integer expectedGrants;
        @JsonProperty("minContribution") String minContribution;
        @JsonProperty("maxContribution") String maxContribution;
        @JsonProperty("deadlineModel") String deadlineModel;
        @JsonProperty("budgetYearMap") Map<String, String> budgetYearMap;
    }

    @Getter
    public static class ActionItem {

        @JsonProperty("types") List<Object> types;
        MGATypeOrSubmissionProcedure submissionProcedure;
    }

    @Getter
    public static class ActionType {
        String typeOfAction;
        List<MGATypeOrSubmissionProcedure> typeOfMGA;

        public static ActionType of(LinkedHashMap<String, Object> map) {
            return new ActionType((String) map.get("typeOfAction"), (List<LinkedHashMap<String, String>>) map.get("typeOfMGA"));
        }

        @JsonCreator
        public ActionType(@JsonProperty("typeOfAction") String typeOfAction, List<LinkedHashMap<String, String>> typeOfMGA) {
            this.typeOfAction = typeOfAction;
            this.typeOfMGA = typeOfMGA.stream().map(m -> new MGATypeOrSubmissionProcedure(m.get("abbreviation"), m.get("description"))).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MGATypeOrSubmissionProcedure {
         @JsonProperty("abbreviation") String abbreviation;
         @JsonProperty("description") String description;
    }

}
