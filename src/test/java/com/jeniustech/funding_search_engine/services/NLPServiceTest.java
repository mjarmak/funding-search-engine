package com.jeniustech.funding_search_engine.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class NLPServiceTest {

    @InjectMocks
    private NLPService nlpService;

    @Test
    void testGetKeywords() throws IOException {
        String text = "TOURINN-01-2020 Innovation uptake and digitalisation in the tourism sector Through this call for applicants, " +
                "the CulTourData project will support tourism SMEs by guiding them to make a better use of data, using creative " +
                "customer-engaging approaches and new technologies. To this end, the CulTourData call will offer grants aiming " +
                "to:support tourism SMEs through the promotion of up-skilling, capacity building and digitalization;foster a " +
                "triple-helix cooperation among tourism SMEs, creatives and data analysis experts;provide ongoing mentoring and " +
                "tutoring support to the awarded SMEs;foster networking at the European level by offering SMEs access to international " +
                "collaboration platforms and networks.CulTourData project will award 9 (nine) lump sum grants of 7,000 EUR to finance " +
                "tourism SMEs and support the following activities:Advisory services that consist in an in-depth mapping of specific " +
                "tourism SMEs' digitalisation / innovation needs and the guidance to adapt the business processes in order to create " +
                "the conditions for the deployment of solutions in the field of data collection, analysis and visualization;Skills " +
                "improvement, consisting in training and coaching activities to improve the employees' digital skills necessary for " +
                "a further digitalization of business processes;Update and Deployment of Technologies, especially emerging " +
                "technologies, necessary for the data collection, analysis and visualization.Territorial Eligibility: Germany, " +
                "Netherlands and Portugal Applications must be submitted at the following page: https://deuscci.eu/first-open-call-tourism-sme/ " +
                "Applicants will need to follow the instructions on the site and fill in all the necessary attachmentsAs per " +
                "the project proposal submitted, the evaluation of each submitted application will be carried out by an International " +
                "Evaluation Panel consisting of 2 experts from the CulTourData consortium partners and one expert from ENAT to " +
                "evaluate the accessibility criterion. These representatives have solid knowledge on topics related to digital " +
                "transformation, smart tourism and capacity building and therefore their views and opinions will ensure that " +
                "the most suitable candidates are selected.Once the applicants have submitted their proposals, the CulTourData " +
                "Evaluation team will proceed to: Check eligibility and admissibility and, if successful; Initiate the evaluation " +
                "of the content of the proposals (Quality check). The purpose of the evaluation is to assess the excellence, " +
                "impact and implementation of each proposal that successfully passed the admissibility and eligibility criteria. " +
                "Participating SMEs will be requested to explain their needs in terms of digitalisation and technical support. " +
                "The purpose of the evaluation is to assess the EXCELLENCE, IMPACT and IMPLEMENTATION of each proposal that " +
                "successfully passed the admissibility and eligibility criteria. The maximum score is 20 for Excellence and 10 " +
                "for Impact and Implementation.Only proposals ranked equal or over 21 points (threshold) and that get at least " +
                "half of the total points under all Evaluation Criteria will be pre-selected.In case of ex-aequos, the priority " +
                "will be given as follows:Score under the Impact sectionScore under Excellence sectionScore under the accessibility " +
                "criteriaGender balance in the project implementation " +
                "3 months " +
                "The Guide for Applicants and Template of Application Form are available at the following link https://deuscci.eu/first-open-call-tourism-sme/";

        List<String> keywords = nlpService.getKeywords(text);
        assertEquals(20, keywords.size());
        assertEquals(
                "[tourism, excellence, eligibility, criterion, applicant, proposal, admissibility, technology, accessibility, need, point, grant, expert, half, consortium, networking, threshold, employee, approach, score]"
                , keywords.toString());

    }
}
