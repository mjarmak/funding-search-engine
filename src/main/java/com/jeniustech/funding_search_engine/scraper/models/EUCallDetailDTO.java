package com.jeniustech.funding_search_engine.scraper.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeniustech.funding_search_engine.exceptions.MapperException;
import lombok.Builder;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Value
public class EUCallDetailDTO {

    TopicDetailsDTO topicDetails;

    @JsonCreator
    public EUCallDetailDTO(@JsonProperty("TopicDetails") TopicDetailsDTO topicDetails) {
        this.topicDetails = topicDetails;
    }

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
    private BudgetItem getBudget() {
        for (Map.Entry<String, List<BudgetItem>> entry : topicDetails.budgetOverview.budgetTopicActionMap.entrySet()) {

            for (BudgetItem budgetItem : entry.getValue()) {
                if (budgetItem.action.equals(topicDetails.identifier + " - " + getActionType())) {
                    return budgetItem;
                }
            }
        }
        throw new MapperException("Budget is not found");
    }

    @JsonIgnore
    public String getMinBudget() {
        if (getBudget().minContribution != null) {
            return getBudget().minContribution;
        }
        return getLastYearBudget();

    }

    @JsonIgnore
    public String getMaxBudget() {
        if (getBudget().maxContribution != null) {
            return getBudget().maxContribution;
        }
        return getLastYearBudget();
    }

    @JsonIgnore
    private String getLastYearBudget() {
        return getBudget().budgetYearMap.values().stream().max(String::compareTo).orElse(null);
    }

    @JsonIgnore
    public Integer getNumberOfGrants() {
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
    public String getConditions() {
        return topicDetails.conditions;
    }

    @JsonIgnore
    public String getSupportInformation() {
        return topicDetails.supportInformation;
    }

    @JsonIgnore
    public String getDestinationDetails() {
        return topicDetails.destinationDetails;
    }

    @JsonIgnore
    public String getMissionDetails() {
        return topicDetails.missionDetails;
    }


    @Value
    @Builder
    public static class TopicDetailsDTO {

        String identifier;
        String title;
        String callTitle;
        String description;
        String destinationDetails;
        String missionDetails;

        String beneficiaryAdministration;
        String furtherInformation;
        String conditions;
        String supportInformation;

        List<ActionItem> actions;

        BudgetOverviewDTO budgetOverview;

        @JsonCreator
        public TopicDetailsDTO(@JsonProperty("identifier") String identifier,
                               @JsonProperty("title") String title,
                               @JsonProperty("callTitle") String callTitle,
                               @JsonProperty("description") String description,
                               String destinationDetails,
                               String missionDetails,
                               String beneficiaryAdministration,
                               String furtherInformation,
                               String conditions,
                               String supportInformation,
                               @JsonProperty("actions") List<ActionItem> actions,
                               @JsonProperty("budgetOverviewJSONItem") BudgetOverviewDTO budgetOverview
        ) {
            this.identifier = identifier;
            this.title = title;
            this.callTitle = callTitle;
            this.description = description;
            this.destinationDetails = destinationDetails;
            this.missionDetails = missionDetails;
            this.beneficiaryAdministration = beneficiaryAdministration;
            this.furtherInformation = furtherInformation;
            this.conditions = conditions;
            this.supportInformation = supportInformation;
            this.actions = actions;
            this.budgetOverview = budgetOverview;
        }
    }

    @Value
    public static class BudgetOverviewDTO {

        Map<String, List<BudgetItem>> budgetTopicActionMap;

        @JsonCreator
        public BudgetOverviewDTO(@JsonProperty("budgetTopicActionMap") Map<String, List<BudgetItem>> budgetTopicActionMap) {
            this.budgetTopicActionMap = budgetTopicActionMap;
        }

    }

    @Value
    public static class BudgetItem {

        String action;
        Integer expectedGrants;
        String minContribution;
        String maxContribution;
        Map<String, String> budgetYearMap;

        @JsonCreator
        public BudgetItem(@JsonProperty("action") String action,
                          @JsonProperty("expectedGrants") Integer expectedGrants,
                          @JsonProperty("minContribution") String minContribution,
                          @JsonProperty("maxContribution") String maxContribution,
                          Map<String, String> budgetYearMap
        ) {
            this.action = action;
            this.expectedGrants = expectedGrants;
            this.minContribution = minContribution;
            this.maxContribution = maxContribution;
            this.budgetYearMap = budgetYearMap;
        }

    }

    @Value
    public static class ActionItem {

        List<Object> types;
        MGATypeOrSubmissionProcedure submissionProcedure;

        @JsonCreator
        public ActionItem(@JsonProperty("types") List<Object> types, MGATypeOrSubmissionProcedure submissionProcedure) {
            this.types = types;
            this.submissionProcedure = submissionProcedure;
        }
    }

    @Value
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

    @Value
    public static class MGATypeOrSubmissionProcedure {
        String abbreviation;
        String description;

        @JsonCreator
        public MGATypeOrSubmissionProcedure(
                @JsonProperty("abbreviation") String abbreviation,
                @JsonProperty("description") String description) {
            this.abbreviation = abbreviation;
            this.description = description;
        }
    }

}
