package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CSVServiceTest {

    @Autowired
    private CSVService csvService;


    @Test
    void writeCSV() {
        List<Project> projects = List.of(
                Project.builder()
                        .referenceId("725172L")
                        .acronym("SIRFUNCT")
                        .status(ProjectStatusEnum.CLOSED)
                        .title("Chemical Tools for Unravelling Sirtuin Function")
                        .startDate(LocalDate.of(2017, 4, 1))
                        .endDate(LocalDate.of(2022, 3, 31))
                        .fundingOrganisation(new BigDecimal("1758742"))
                        .fundingEU(new BigDecimal("1758742"))
                        .legalBasis("H2020-EU.1.1.")
                        .call(Call.builder().identifier("ERC-2016-COG").build())
                        .signDate(LocalDate.of(2017, 3, 16))
                        .masterCallIdentifier("H2020")
                        .fundingScheme(FundingSchemeEnum.ERA_NET_Cofund)
                        .longTexts(List.of(
                                LongText.builder()
                                        .type(LongTextTypeEnum.PROJECT_OBJECTIVE)
                                        .text("test$ then $")
                                        .build()
                        ))
                        .build()
        );
        csvService.writeProjectsCSV(projects, "C:\\Projects\\funding-search-engine\\src\\test\\resources\\data\\projects\\project_2014.csv");
    }

    @Test
    void testPreprocessCSV_1() {
//        String input = "\" mode.The PMS market is estimated at $1.5 billion in 2016 and expected to grow to $2.0 billion in 2024.DAYUSE targets its gradual smooth adoption, first as a complementary tool to existing PMS and finally as a full substitute. DAYUSE will then extend its reach to the whole Hospitality Industry while integrating other distributors. The company wants to be the European game changer in an industry where North American players prevail.This SME Instrument Phase 2 project represents for DAYUSE the very last step to the market. For 24 months, it will prepare and fine-tune the IT tools for the industrialization while running pilots globally in different countries (Europe, US, Asia) to be prepared for the challenges of the Go-to-Market execution in Phase 3.\"";
        String input = "\"823217\";\"DAYUSE\";\"CLOSED\";\"DAYUSE: the European game changer that creates a disruptive value-adding exploitation model for Hotels.\";\"2018-07-01\";\"2020-06-30\";\"2154187,5\";\"1507931,25\";\"H2020-EU.2.3.\";\"EIC-SMEInst-2018-2020\";\"2018-06-21\";\"H2020\";\"H2020-EIC-SMEInst-2018-2020\";\"H2020-SMEInst-2018-2020-2\";\"SME-2\";\"\";\"\"\"Created in late 2010, dayuse.com is the innovative world leader in online reservation for the emerging \"\"\"\"day use\"\"\"\" hotel room segment in a typical \"\"\"\"Blue Ocean\"\"\"\" strategy.It provides an answer to this key question \"\"\"\"Why should I book a hotel room for a full overnight price when I only need it for a few hours?\"\"\"\"The Company now relies on a network of over 4,500 partner hotels. The service is provided on a day-time allotment pricing basis. DAYUSE  boasts over 300,000 bookings p.a. which represents now one reservation per minute!With the recent signature of agreements with major hotel chains such as AccorHotels, Hilton, Marriott that want to open their huge hotel networks to dayuse.com, a large-scale industrialization phase looks urgent.DAYUSE has now the ambition to leverage on its leader position in the \"\"\"\"day use\"\"\"\" segment to shake completely the Hospitality Industry by imposing gradually and globally a new scalable industry standard based on an hourly service/rate, 24 hours a day, similarly to industries like car renting.Concretely, the company targets to provide to hotels a DAYUSE Proprietary Management System (PMS) called DMS delivered through SaaS with a ground-breaking pricing \"\"\"\"per transaction\"\"\"\" mode.The PMS market is estimated at $1.5 billion in 2016 and expected to grow to $2.0 billion in 2024.DAYUSE targets its gradual smooth adoption, first as a complementary tool to existing PMS and finally as a full substitute. DAYUSE will then extend its reach to the whole Hospitality Industry while integrating other distributors. The company wants to be the European game changer in an industry where North American players prevail.This SME Instrument Phase 2 project represents for DAYUSE the very last step to the market. For 24 months, it will prepare and fine-tune the IT tools for the industrialization while running pilots globally in different countries (Europe, US, Asia) to be prepared for the challenges of the Go-to-Market execution in Phase 3.\"\"\";\"2024-03-13 16:30:22\";\"217048\";\"10.3030/823217\"";
        String output = csvService.processCSVLine(input);
        assertEquals("\"823217\";\"DAYUSE\";\"CLOSED\";\"DAYUSE: the European game changer that creates a disruptive value-adding exploitation model for Hotels.\";\"2018-07-01\";\"2020-06-30\";\"2154187,5\";\"1507931,25\";\"H2020-EU.2.3.\";\"EIC-SMEInst-2018-2020\";\"2018-06-21\";\"H2020\";\"H2020-EIC-SMEInst-2018-2020\";\"H2020-SMEInst-2018-2020-2\";\"SME-2\";\"\";\"\"\"Created in late 2010, dayuse.com is the innovative world leader in online reservation for the emerging \"\"\"\"day use\"\"\"\" hotel room segment in a typical \"\"\"\"Blue Ocean\"\"\"\" strategy.It provides an answer to this key question \"\"\"\"Why should I book a hotel room for a full overnight price when I only need it for a few hours?\"\"\"\"The Company now relies on a network of over 4,500 partner hotels. The service is provided on a day-time allotment pricing basis. DAYUSE  boasts over 300,000 bookings p.a. which represents now one reservation per minute!With the recent signature of agreements with major hotel chains such as AccorHotels, Hilton, Marriott that want to open their huge hotel networks to dayuse.com, a large-scale industrialization phase looks urgent.DAYUSE has now the ambition to leverage on its leader position in the \"\"\"\"day use\"\"\"\" segment to shake completely the Hospitality Industry by imposing gradually and globally a new scalable industry standard based on an hourly service/rate, 24 hours a day, similarly to industries like car renting.Concretely, the company targets to provide to hotels a DAYUSE Proprietary Management System (PMS) called DMS delivered through SaaS with a ground-breaking pricing \"\"\"\"per transaction\"\"\"\" mode.The PMS market is estimated at $1.5 billion in 2016 and expected to grow to $2.0 billion in 2024.DAYUSE targets its gradual smooth adoption, first as a complementary tool to existing PMS and finally as a full substitute. DAYUSE will then extend its reach to the whole Hospitality Industry while integrating other distributors. The company wants to be the European game changer in an industry where North American players prevail.This SME Instrument Phase 2 project represents for DAYUSE the very last step to the market. For 24 months, it will prepare and fine-tune the IT tools for the industrialization while running pilots globally in different countries (Europe, US, Asia) to be prepared for the challenges of the Go-to-Market execution in Phase 3.\"\"\";\"2024-03-13 16:30:22\";\"217048\";\"10.3030/823217\""
                , output);
    }

    @Test
    void testPreprocessCSV_2() {
        String input = "\"John; Doe\";\"30\";\"New York; USA\"";
        String output = csvService.processCSVLine(input);
        assertEquals("\"John\\; Doe\";\"30\";\"New York\\; USA\"", output);
    }

    @Test
    void testPreprocessCSV_3() {
        String input = "\"725172\";\"SIRFUNCT\";\"CLOSED\";\"Chemical Tools for Unravelling Sirtuin Function\";\"2017-04-01\";\"2022-03-31\";\"1758742\";\"1758742\";\"H2020-EU.1.1.\";\"ERC-2016-COG\";\"2017-03-16\";\"H2020\";\"ERC-2016-COG\";\"ERC-2016-COG\";\"ERC-COG\";\"\";\"It was recently realized that lysine acetylation affects a wide variety of cellular processes in addition to the initially recognized histone related gene regulation. Together with recent groundbreaking results, revealing the presence of additional acyllysine modifications, the basis for a paradigm shift in this area was formed. Examples of enzymes formerly thought to be lysine deacetylases, have been shown to cleave these new types of lysine modification and members of the sirtuin class of enzymes play a central role.   Development of new tools to investigate the importance of these new modifications as well as the sirtuins that cleave them is required. We therefore propose to adopt an interdisciplinary approach by developing selective inhibitors and so-called activity-based probes (ABPs) and applying these to the investigation of proteins recognizing novel post-translational acylations of lysine residues in cells. Such ABPs will be powerful tools for providing insight regarding this rapidly evolving area of biochemistry; however, the current state-of-the-art in ABP design is endowed with severe limitations because the modifications are inherently cleaved by various hydrolases in human cells. Thus, in the present project, I propose that novel designs accommodating non-cleavable modifications are warranted to maintain structural integrity during experiments.    Furthermore, I propose to apply similar mechanism-based designs to develop potent and isoform-selective sirtuin inhibitors, which will serve as chemical probes to investigate links between cancer and metabolism, and may ultimately serve as lead compounds for pre-clinical pharmaceutical development. AIM-I. (a) Development and (b) application of collections of chemical probes for activity-based investigation of enzymes that interact with post-translationally acylated proteins.AIM-II. Utilization of structural and mechanistic insight to design potent and selective inhibitors of sirtuin enzymes.\";\"2022-12-11 17:16:42\";\"209149\";\"10.3030/725172\"";
        String output = csvService.processCSVLine(input);
        assertEquals("\"725172\";\"SIRFUNCT\";\"CLOSED\";\"Chemical Tools for Unravelling Sirtuin Function\";\"2017-04-01\";\"2022-03-31\";\"1758742\";\"1758742\";\"H2020-EU.1.1.\";\"ERC-2016-COG\";\"2017-03-16\";\"H2020\";\"ERC-2016-COG\";\"ERC-2016-COG\";\"ERC-COG\";\"\";\"It was recently realized that lysine acetylation affects a wide variety of cellular processes in addition to the initially recognized histone related gene regulation. Together with recent groundbreaking results, revealing the presence of additional acyllysine modifications, the basis for a paradigm shift in this area was formed. Examples of enzymes formerly thought to be lysine deacetylases, have been shown to cleave these new types of lysine modification and members of the sirtuin class of enzymes play a central role.   Development of new tools to investigate the importance of these new modifications as well as the sirtuins that cleave them is required. We therefore propose to adopt an interdisciplinary approach by developing selective inhibitors and so-called activity-based probes (ABPs) and applying these to the investigation of proteins recognizing novel post-translational acylations of lysine residues in cells. Such ABPs will be powerful tools for providing insight regarding this rapidly evolving area of biochemistry\\; however, the current state-of-the-art in ABP design is endowed with severe limitations because the modifications are inherently cleaved by various hydrolases in human cells. Thus, in the present project, I propose that novel designs accommodating non-cleavable modifications are warranted to maintain structural integrity during experiments.    Furthermore, I propose to apply similar mechanism-based designs to develop potent and isoform-selective sirtuin inhibitors, which will serve as chemical probes to investigate links between cancer and metabolism, and may ultimately serve as lead compounds for pre-clinical pharmaceutical development. AIM-I. (a) Development and (b) application of collections of chemical probes for activity-based investigation of enzymes that interact with post-translationally acylated proteins.AIM-II. Utilization of structural and mechanistic insight to design potent and selective inhibitors of sirtuin enzymes.\";\"2022-12-11 17:16:42\";\"209149\";\"10.3030/725172\"", output);
    }

    @Test
    void testPreprocessCSV_4() {
        String input = "\"test$ then $\"";
        String output = csvService.processCSVLine(input);
        assertEquals("\"test$ then $\"", output);
    }

}
