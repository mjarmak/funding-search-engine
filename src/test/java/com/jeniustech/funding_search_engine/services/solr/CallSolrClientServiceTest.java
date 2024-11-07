package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import com.jeniustech.funding_search_engine.enums.SubscriptionStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.LogBookRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.enums.StatusFilterEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CallSolrClientServiceTest {

    private final JwtModel JWT_MODEL = JwtModel.builder().userId("userId").build();

    @Autowired
    private CallSolrClientService service;

    @MockBean
    private UserDataRepository userDataRepository;

    @MockBean
    private LogBookRepository logBookRepository;

    @BeforeEach
    public void setUp() {
        UserData userData = UserData.builder()
                .subjectId("userId")
                .userSubscriptionJoins(List.of(
                        UserSubscriptionJoin.builder().
                                type(SubscriptionJoinType.ADMIN)
                                .subscription(
                                        UserSubscription.builder()
                                                .status(SubscriptionStatusEnum.ACTIVE)
                                                .type(SubscriptionTypeEnum.ENTERPRISE)
                                                .endDate(DateMapper.mapToTimestamp("9999-01-01T01:01:01"))
                                                .build()
                                ).build()))
                .build();
        when(userDataRepository.findBySubjectId(JWT_MODEL.getUserId())).thenReturn(Optional.of(userData));
        when(logBookRepository.save(any())).thenReturn(null);
    }

    @Test
    public void testSearch_1() {
        SearchDTO<CallDTO> results = service.search("Digital solutions to foster participative design, planning and management of buildings, neighbourhoods and urban districts",
                0, 5, List.of(UPCOMING,OPEN,CLOSED), JWT_MODEL);

        assertEquals("HORIZON-CL5-2024-D4-02-05", results.getResults().get(0).getIdentifier());
        assertEquals("Digital solutions to foster participative design, planning and management of buildings, neighbourhoods and urban districts (Built4People Partnership)", results.getResults().get(0).getTitle());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HORIZON-CL5-2024-D4-02-05",
            "SMP-COSME-2021-CLUSTER-01",
            "HORIZON-CL4-2022-RESILIENCE-01-26",
            "HORIZON-CL5-2024-D3-02-02",
            "HORIZON-CL5-2024-D3-02-13",
            "CREA-CROSS-2025-MFP",
            "CERV-2025-OG-FPA",
            "HORIZON-CL4-2022-RESILIENCE-01-26",
            "HORIZON-CL4-2024-HUMAN-02-34",
            "HORIZON-EIC-2024-PATHFINDERCHALLENGES-01-02",
            "HORIZON-EIC-2024-PATHFINDERCHALLENGES-01-05",
            "HORIZON-EIE-2024-CONNECT-02-01",
            "HORIZON-CL4-2022-RESILIENCE-01-21",
            "HORIZON-JU-SNS-2022-STREAM-C-01-01",
            "SMP-COSME-2022-TOURSME-01",
            "HORIZON-CL5-2021-D2-01-16",
            "HORIZON-CL6-2023-CircBio-01-12",
            "HORIZON-EIC-2024-ACCELERATOR-01",
            "SMP-COSME-2021-CLUSTER-01",
            "EUBA-EFSA-PLANTS-2024-03",
            "DIGITAL-ECCC-2022-CYBER-03-NAT-COORDINATION",
            "DIGITAL-2022-CLOUD-AI-03-DS-SMART",
            "HORIZON-WIDERA-2023-ACCESS-06-01",
            "ERC-2024-POC",
            "HORIZON-CL4-2022-HUMAN-02-02",
            "HORIZON-CL6-2021-CLIMATE-01-02",
            "HORIZON-JU-SNS-2022-STREAM-D-01-01",
            "HORIZON-CL6-2023-GOVERNANCE-01-13",
            "HORIZON-MISS-2022-OCEAN-01-08",
            "HORIZON-MISS-2022-OCEAN-01-08",
            "DIGITAL-ECCC-2022-CYBER-03-NAT-COORDINATION",
            "I3-2021-INV2a-MANU",
            "HORIZON-CL5-2021-D3-01-04",
            "HORIZON-EIT-2023-25-KIC-EITMANUFACTURING",
            "HORIZON-EIC-2024-TRANSITIONOPEN-01",
            "HORIZON-JU-CBE-2024-IAFlag-01",
            "HORIZON-JU-IHI-2024-08-02-two-stage",
            "HORIZON-JU-CBE-2024-IAFlag-02",
            "HORIZON-JU-CBE-2024-CSA-03",
            "HORIZON-JU-CBE-2024-CSA-02",
            "HORIZON-JU-CBE-2024-IA-01",
            "HORIZON-JU-CBE-2024-CSA-01",
            "HORIZON-JU-CBE-2024-IA-02",
            "HORIZON-CL4-2022-HUMAN-01-07",
            "HORIZON-JU-SNS-2023-STREAM-D-01-01",
            "HORIZON-HLTH-2024-DISEASE-09-01",
            "HORIZON-INFRA-2024-EOSC-02-01",
            "HORIZON-JU-CBE-2024-IAFlag-03",
            "HORIZON-INFRA-2024-DEV-02-01",
            "HORIZON-JU-CBE-2024-RIA-03",
            "HORIZON-JU-CBE-2024-RIA-05",
            "EuropeAid/182766/DD/ACT/Multi",
            "EuropeAid/181973/DD/ACT/MG",
            "HORIZON-JU-CBE-2024-RIA-01",
            "HORIZON-JU-CBE-2024-RIA-02",
            "HORIZON-JU-CBE-2024-RIA-04",
            "HORIZON-JU-CBE-2024-IA-07",
            "HORIZON-JU-CBE-2024-IA-03",
            "HORIZON-JU-CBE-2024-IA-06",
            "HORIZON-JU-CBE-2024-IA-05",
            "HORIZON-JU-CBE-2024-IA-04",
            "HORIZON-EURATOM-2024-NRT-01-01",
            "HORIZON-EURATOM-2024-NRT-01-02",
            "EuropeAid/182901/DD/ACT/MK",
            "HORIZON-MISS-2024-CLIMA-01-03",
            "HORIZON-MISS-2024-CLIMA-01-01",
            "HORIZON-MISS-2024-CANCER-01-03",
            "HORIZON-MISS-2024-CLIMA-01-07",
            "HORIZON-WIDERA-2024-ERA-02-01",
            "HORIZON-WIDERA-2024-ERA-02-03",
            "HORIZON-MISS-2024-OCEAN-02-02",
            "HORIZON-MISS-2024-OCEAN-01-05",
            "HORIZON-MISS-2024-CIT-02-01",
            "HORIZON-MISS-2024-CLIMA-01-02",
            "HORIZON-MISS-2024-OCEAN-01-03",
            "LIFE-2021-CET-EUCF",
            "EuropeAid/182950/ID/ACT/RS",
            "LC-SC3-SCC-1-2018-2019-2020",
            "HORIZON-EIT-2023-25-KIC-EITURBANMOBILITY",
            "HORIZON-JU-SNS-2022-STREAM-D-01-01",
            "HORIZON-INFRA-2022-EOSC-01-04",
            "HORIZON-EIT-2023-2025-HEI",
            "EuropeAid/182921/ID/ACT/JO",
            "HORIZON-CL6-2023-FARM2FORK-01-20",
            "DIGITAL-ECCC-2022-CYBER-03-NAT-COORDINATION",
            "EuropeAid/182949/DD/ACT/MD",
            "HORIZON-EIT-2023-25-KIC-EITRAWMATERIALS",
            "HORIZON-CL4-2022-DIGITAL-EMERGING-02-07",
            "HORIZON-INFRA-2022-SERV-B-01-01",
            "EuropeAid/182750/DD/ACT/MD",
            "DIGITAL-Chips-2024-SG-CCC-1",
            "DIGITAL-ECCC-2022-CYBER-03-NAT-COORDINATION",
            "HORIZON-CL4-2021-HUMAN-01-25",
            "EuropeAid/182493/DD/ACT/MD",
            "EuropeAid/182814/DD/ACT/XK",
            "SMP-COSME-2021-CLUSTER-01",
            "HORIZON-CL4-2023-DIGITAL-EMERGING-01-02",
            "HORIZON-CL4-2023-HUMAN-01-12",
            "HORIZON-CL4-2023-HUMAN-01-11",
            "HORIZON-EIT-2023-25-KIC-EITMANUFACTURING",
            "HORIZON-CL4-2023-HUMAN-01-12",
            "HORIZON-CL4-2023-HUMAN-01-12",
            "DIGITAL-ECCC-2023-DEPLOY-CYBER-04-SUPPORT-ASSIST",
            "HORIZON-CL4-2024-HUMAN-03-01",
            "HORIZON-CL4-2024-HUMAN-03-03",
            "HORIZON-CL4-2024-HUMAN-03-04",
            "HORIZON-CL4-2024-HUMAN-02-36",
            "HORIZON-CL6-2022-FARM2FORK-02-04-two-stage",
            "HORIZON-CL5-2024-D2-02-01",
            "HORIZON-WIDERA-2024-TALENTS-03-01",
            "CREA-MEDIA-2025-DEVSLATE",
            "CREA-CULT-2025-LIT",
            "SMP-COSME-2024-SEE-01",
            "CREA-MEDIA-2025-MEDIA360",
            "SMP-COSME-2024-CLUSTER-01",
            "CREA-MEDIA-2025-FILMOVE",
            "CREA-MEDIA-2025-TRAINING",
            "SMP-COSME-2024-SEE-02",
            "CREA-MEDIA-2025-DEVVGIM",
            "CREA-MEDIA-2025-INNOVBUSMOD",
            "ERC-2025-COG",
            "CREA-MEDIA-2025-TVONLINE",
            "ERASMUS-EDU-2024-NARIC",
            "ERASMUS-EDU-2024-EHEA",
            "HORIZON-EUSPA-2023-SPACE-01-44",
            "HORIZON-JU-Chips-FPA-QAC-1",
            "HORIZON-EIE-2024-CONNECT-02-02",
            "LIFE-2025-TA-CAP",
            "ERC-2024-ADG",
            "HORIZON-JU-Chips-FPA-QAC-2",
            "EMFAF-2025-PIA-FLAGSHIP-OCEANFARMING",
            "EMFAF-2025-PIA-FLAGSHIP-I3",
            "EuropeAid/182996/ID/ACT/TR",
            "EuropeAid/182636/DD/ACT/GW",
            "HORIZON-CL6-2021-BIODIV-02-01",
            "HORIZON-MISS-2022-OCEAN-01-08",
            "CEF-T-2024-SUSTMOBGEN-MULTHUB-Studies",
    })
    public void testSearch_identifier(String query) {
        SearchDTO<CallDTO> results = service.search(query, 0, 5, List.of(UPCOMING,OPEN,CLOSED), JWT_MODEL);
        assertEquals(query, results.getResults().get(0).getIdentifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Euroclusters for Europe's recovery",
            "'Innovate to transform' support for SME's sustainability transition (CSA)",
            "Development of next generation synthetic renewable fuel technologies",
            "Support to the activities of the SET Plan Key Action area Renewable fuels and bioenergy",
            "NEWS - Media Freedom Rapid Response Mechanism",
            "Call for proposals for 3-year framework partnership agreements to support European networks, civil society organisations active at EU level and European think tanks in the areas of Union values",
            "'Innovate to transform' support for SME's sustainability transition (CSA)",
            "Towards cement and concrete as a carbon sink",
            "Strengthening the sustainability and resilience of EU space infrastructure",
            "Expanding Academia-Enterprise Collaborations",
            "Leveraging standardisation in Digital Technologies (CSA)",
            "SNS experimental Infrastructure",
            "Sustainable growth and building resilience in tourism - empowering SMEs to carry out the twin transition",
            "Co-Funded Partnership: Driving Urban Transitions to a sustainable future (DUT)",
            "Optimising the sustainable production of wood and non-wood products in small forest properties and development of new forest-based value chains",
            "EIC Accelerator 2024 - Short application",
            "Euroclusters for Europe's recovery",
            "Commodity risk assessment for ornamental plants and plant products",
            "Deploying The Network Of National Coordination Centres With Member States",
            "Data space for smart communities (deployment)",
            "Hop on Facility",
            "ERC PROOF OF CONCEPT GRANT",
            "European Network of AI Excellence Centres: Expanding the European AI lighthouse (RIA)",
            "European Partnership Water Security for the Planet (Water4All)",
            "SNS Large Scale Trials and Pilots (LST&Ps) with Verticals",
            "Open source solutions for edge, cloud and mixed model applications to strengthen production and administrative capacities in agriculture",
            "Student and school activities for the promotion of education on 'blue' sustainability and the protection of marine and freshwater ecosystems",
            "Student and school activities for the promotion of education on 'blue' sustainability and the protection of marine and freshwater ecosystems",
            "Deploying The Network Of National Coordination Centres With Member States",
            "Innovation investments Strand 2a - MANU",
            "Clean Energy Transition",
            "EIT Manufacturing Business Plan 2023-2025",
            "Transition Open 2024",
            "Bio-based value chains for valorisation of sustainable oil crops",
            "Novel Endpoints for Osteoarthritis (OA) by applying Big Data Analytics",
            "Bio-based dedicated platform chemicals via cost-effective, sustainable and resource-efficient conversion of biomass",
            "Supporting the CBE JU Deployment Group on Primary Producers",
            "Mobilize inclusive participation in bio-based systems and supporting the CBE JU widening strategy and its action plan",
            "Bio-based materials and products for biodegradable in-soil applications",
            "New forms of cooperation in agriculture and the forest-based sector",
            "Sustainable micro-algae as feedstock for innovative, added-value applications",
            "NGI International Collaboration - USA and Canada (RIA)",
            "SNS Large Scale Trials and Pilots (LST&Ps) with Verticals - Focused Topic",
            "European Partnership: One Health Anti-Microbial Resistance",
            "Future Engagement Model for the EOSC Federation",
            "Bio-based value chains for valorisation of sustainable natural fibre feedstock",
            "Assessing the state of research infrastructures in Ukraine",
            "Sustainable, bio-based alternatives for crop protection",
            "Innovative bio-based food/feed ingredients",
            "Appui � la Gestion des Aires Prot�g�es dans les paysages prioritaires au Gabon",
            "Programme RESILIENCE Grand Sud - volet ONG",
            "Valorisation of polluted/contaminated wood from industrial and post-consumer waste streams",
            "Biotech routes to obtain bio-based chemicals/ materials replacing animal-derived ones",
            "SSbD bio-based coating materials for applications under demanding and/or extreme conditions",
            "Innovative conversion of biogenic gaseous carbon into bio-based chemicals, ingredients, materials",
            "Enlarging the portfolio of commercially produced bio-based SSbD solvents",
            "Innovative bio-based adhesives and binders for circular products meeting market requirements",
//            "Selective and sustainable (co)-production of lignin-derived aromatics",
            "Circular and SSbD bio-based construction & building materials with functional properties",
            "European Fusion Industry Platform and preparation for a Public-Private Partnership on Fusion Energy",
            "European Nuclear Skills Initiative",
            "MK 21 IPA JH 01 24 TWL Agency for protection of the right to free access to public information (APRFAPI) North Macedonia",
            "Develop and refine outcome indicators to measure progress on climate resilience at national, regional and local levels, including knowledge and feedback developed from the Mission",
            "Bringing available and actionable solutions for climate adaptation to the knowledge of the regions and local authorities",
            "Accessible and affordable tests to advance early detection of heritable cancers in European regions",
            "Demonstration of solutions specifically suited to rural areas and small/ medium size population local communities",
            "Experimentation and exchange of good practices for value creation",
            "Talent ecosystems for attractive early research careers - pilot",
            "Support for the Coalition of waterfront cities, regions and islands for Mission Ocean and Waters",
            "Our Blue Future - Co-designing a future vision of a restored ocean and water system in the EU by 2030 and 2050",
            "Supporting national, regional and local authorities across Europe to prepare for the transition towards climate neutrality within cities",
            "Bringing together the national level with the engaged regional and local levels (multi-level governance)",
            "Atlantic and Arctic sea basin lighthouse, Mediterranean Sea basin lighthouse, Baltic and North Sea basin lighthouse - Reducing the environmental impacts of fisheries on marine species and habitats",
            "European City Facility",
            "Further upgrade of education function in the Tax Administration of the Republic of Serbia",
            "EIT Urban Mobility Business Plan 2023-2025",
            "SNS Large Scale Trials and Pilots (LST&Ps) with Verticals",
            "Support for initiatives helping to generate global standards, specifications and recommendations for open sharing of FAIR research data, publications and software",
            "Higher Education Institutions (HEI) initiative 2023-2025",
            "Support to the Public Security Directorate to Fight Against Organised Crime",
            "EU-Africa Union - food safety",
            "Deploying The Network Of National Coordination Centres With Member States",
            "Strengthening the institutional capacities of the State Labour Inspectorate to enforce the labour standards in the Republic of Moldova in line with the EU best practices",
            "EIT Raw Materials Business Plan 2023-2025",
            "Increased robotics capabilities demonstrated in key sectors (AI, Data and Robotics Partnership) (IA)",
            "Implementing digital services to empower neuroscience research for health and brain inspired technology via EBRAINS",
            "Support to Civil Society Organisations in the Republic of Moldova in the field of Home Affairs - Security and Migration",
            "Competence Centers",
            "Deploying The Network Of National Coordination Centres With Member States",
            "eXtended Collaborative Telepresence (IA)",
            "Strengthen the capacities of the General Inspectorate for Migration for the implementation of the migration management and asylum legislation in line with EU acquis",
            "Cross-border programme Kosovo* - North Macedonia under IPA III (2023 allocation)",
            "Euroclusters for Europe's recovery",
            "Industrial leadership in AI, Data and Robotics - advanced human robot interaction (AI Data and Robotics Partnership) (IA)",
            "Pilots for the Next Generation Internet (IA)",
            "Next Generation Internet Fund (RIA)",
            "EIT Manufacturing Business Plan 2023-2025",
            "Pilots for the Next Generation Internet (IA)",
            "Pilots for the Next Generation Internet (IA)",
            "Preparedness support and mutual assistance",
            "Advancing Large AI Models: Integration of New Data Modalities and Expansion of Capabilities (AI, Data and Robotics Partnership) (RIA)",
            "Digital Humanism - Putting people at the centre of the digital transformation (CSA)",
            "Facilitate the engagement in global ICT standardisation development (CSA)",
            "Synergy with national and regional initiatives in Europe on Innovative Materials (CSA)",
            "Smart solutions for the use of digital technologies for small- and medium-sized, farms and farm structures",
            "Sustainable high-throughput production processes for stable lithium metal anodes for next generation batteries (Batt4EU Partnership)",
            "ERA Talents",
            "European slate development",
            "Circulation of European literary works",
            "Stepping up organisational and entrepreneurial capacity of SMEs in social economy",
            "MEDIA 360�",
            "Joint Cluster Initiatives (EUROCLUSTERS) for Europe's recovery",
            "Films on the Move",
            "SKILLS AND TALENT DEVELOPMENT",
            "Partnerships for circular value chains between mainstream businesses and SMEs in social economy",
            "Video game and immersive content development",
            "Innovative tools and business models",
            "ERC CONSOLIDATOR GRANTS",
            "TV and online content",
            "National Academic Recognition Information Centres (NARIC)",
            "European Higher Education Area (EHEA)",
            "The Galileo PRS service for governmental authorised use cases",
            "Quantum Chip Technology for stability Pilots",
            "Mutual learning and support scheme for national and regional innovation programmes"
    })
    void testSearch_title(String query) {
        SearchDTO<CallDTO> results = service.search(query, 0, 5, List.of(UPCOMING,OPEN,CLOSED), JWT_MODEL);
        assertEquals(query.toLowerCase(), results.getResults().get(0).getTitle().toLowerCase());
    }

    @Test
    public void testSearch_2() {
        SearchDTO<CallDTO> results = service.search("digital solutions", 0, 5, List.of(UPCOMING,OPEN,CLOSED), JWT_MODEL);
        System.out.println("count: " + results.getTechnicalTotalResults());
        assertTrue(results.getTechnicalTotalResults() > 1000);
    }
}
