package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.constants.excel.OrganisationColumns;
import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.exceptions.OrganisationNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ProjectNotFoundException;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.util.ProjectDataLoader.getFundingOrganisation;
import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class OrganisationDataLoader {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    OrganisationRepository organisationRepository;

    //    @Test
    void loadData() {
        String excelFilePath = "C:/Projects/funding-search-engine/src/test/resources/data/calls/MasterExcelSheet.xlsx";

        try (FileInputStream fis = new FileInputStream(new ClassPathResource(excelFilePath).getFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            // get headers
            int PROJECT_ID_INDEX = 0;
            int ORGANISATION_ID_INDEX = 0;
            int VAT_NUMBER_INDEX = 0;
            int NAME_INDEX = 0;
            int SHORT_NAME_INDEX = 0;
            int SME_INDEX = 0;
            int ACTIVITY_TYPE_INDEX = 0;
            int STREET_INDEX = 0;
            int POSTCODE_INDEX = 0;
            int CITY_INDEX = 0;
            int COUNTRY_INDEX = 0;
            int NUTS_CODE_INDEX = 0;
            int GEO_LOCATION_INDEX = 0;
            int ORGANIZATION_URL_INDEX = 0;
            int CONTACT_FORM_INDEX = 0;
            int CONTENTUPDATEDATE_INDEX = 0;
            int RCN_INDEX = 0;
            int ORDER_INDEX = 0;
            int ROLE_INDEX = 0;
            int NET_EC_CONTRIBUTION_INDEX = 0;
            int TOTAL_COST_INDEX = 0;

            var headers = sheet.getRow(0);
            for (Cell cell : headers) {
                switch (cell.getStringCellValue().toLowerCase().replace(" ", "_")) {
                    case OrganisationColumns.PROJECT_ID -> PROJECT_ID_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.ORGANISATION_ID -> ORGANISATION_ID_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.VAT_NUMBER -> VAT_NUMBER_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.NAME -> NAME_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.SHORT_NAME -> SHORT_NAME_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.SME -> SME_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.ACTIVITY_TYPE -> ACTIVITY_TYPE_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.STREET -> STREET_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.POSTCODE -> POSTCODE_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.CITY -> CITY_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.COUNTRY -> COUNTRY_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.NUTS_CODE -> NUTS_CODE_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.GEO_LOCATION -> GEO_LOCATION_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.ORGANIZATION_URL -> ORGANIZATION_URL_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.CONTACT_FORM -> CONTACT_FORM_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.CONTENTUPDATEDATE -> CONTENTUPDATEDATE_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.RCN -> RCN_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.ORDER -> ORDER_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.ROLE -> ROLE_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.NET_EC_CONTRIBUTION -> NET_EC_CONTRIBUTION_INDEX = cell.getColumnIndex();
                    case OrganisationColumns.TOTAL_COST -> TOTAL_COST_INDEX = cell.getColumnIndex();
                }
            }

            if (
                    List.of(
                            PROJECT_ID_INDEX,
                            ORGANISATION_ID_INDEX,
                            VAT_NUMBER_INDEX,
                            NAME_INDEX,
                            SHORT_NAME_INDEX,
                            SME_INDEX,
                            ACTIVITY_TYPE_INDEX,
                            STREET_INDEX,
                            POSTCODE_INDEX,
                            CITY_INDEX,
                            COUNTRY_INDEX,
                            NUTS_CODE_INDEX,
                            GEO_LOCATION_INDEX,
                            ORGANIZATION_URL_INDEX,
                            CONTACT_FORM_INDEX,
                            CONTENTUPDATEDATE_INDEX,
                            RCN_INDEX,
                            ORDER_INDEX,
                            ROLE_INDEX,
                            NET_EC_CONTRIBUTION_INDEX,
                            TOTAL_COST_INDEX
                    ).contains(0)
            ) {
                System.out.println("Header not found");
                fail();
            }

            for (Row row : sheet) {
                System.out.println("Row: " + row.getRowNum());
                if (row.getRowNum() == 0) {
                    continue; // skip headers
                }

                Organisation organisation = Organisation.builder()
                        .referenceId((long) row.getCell(ORGANISATION_ID_INDEX).getNumericCellValue())
                        .vatNumber(row.getCell(VAT_NUMBER_INDEX).getStringCellValue())
                        .name(row.getCell(NAME_INDEX).getStringCellValue())
                        .shortName(row.getCell(SHORT_NAME_INDEX).getStringCellValue())
                        .sme(BooleanEnum.fromBoolean(row.getCell(SME_INDEX).getBooleanCellValue()))
                        .nutsCode(row.getCell(NUTS_CODE_INDEX).getStringCellValue())
                        .fundingOrganisation(getFundingOrganisation(TOTAL_COST_INDEX, NET_EC_CONTRIBUTION_INDEX, row))
                        .fundingEU(new BigDecimal(getBudget(TOTAL_COST_INDEX, row)))
                        .rcn(row.getCell(RCN_INDEX).getStringCellValue())
                        .type(OrganisationTypeEnum.valueOf(row.getCell(ACTIVITY_TYPE_INDEX).getStringCellValue()))
                        .build();

                // set project, organisationProjectJoins
                setProjectLink(PROJECT_ID_INDEX, ROLE_INDEX, row, organisation);

                // set address
                Address address = Address.builder()
                        .street(row.getCell(STREET_INDEX).getStringCellValue())
                        .postCode(row.getCell(POSTCODE_INDEX).getStringCellValue())
                        .city(row.getCell(CITY_INDEX).getStringCellValue())
                        .country(CountryEnum.valueOf(row.getCell(COUNTRY_INDEX).getStringCellValue()))
                        .build();
                organisation.setAddress(address);

                // set locationCoordinates
                String longitudeLatitude = row.getCell(GEO_LOCATION_INDEX).getStringCellValue();
                if (isNotEmpty(longitudeLatitude)) {
                    String[] coordinates = longitudeLatitude.split(",");
                    LocationCoordinates locationCoordinates = LocationCoordinates.builder()
                            .y(coordinates[0])
                            .x(coordinates[1])
                            .build();
                    organisation.setLocationCoordinates(locationCoordinates);
                }

                // set contactInfos
                String url = row.getCell(ORGANIZATION_URL_INDEX).getStringCellValue();
                OrganisationContactInfo urlContactInfo = OrganisationContactInfo.builder()
                        .type(ContactInfoTypeEnum.URL)
                        .value(url)
                        .organisation(organisation)
                        .build();
                organisation.setContactInfos(List.of(urlContactInfo));

                Optional<Organisation> existingOrganisationOptional = organisationRepository.findByReferenceId(organisation.getReferenceId());
                if (existingOrganisationOptional.isPresent()) {
                    Organisation existingOrganisation = existingOrganisationOptional.get();

                    updateProjectLinks(organisation, existingOrganisation);
                    updateAddress(organisation, existingOrganisation);
                    updateContactInfos(organisation, existingOrganisation);
                    updateCoordinates(organisation, existingOrganisation);

                    if (isNotEmpty(existingOrganisation.getReferenceId())) {
                        existingOrganisation.setReferenceId(organisation.getReferenceId());
                    }
                    if (isNotEmpty(existingOrganisation.getRcn())) {
                        existingOrganisation.setRcn(organisation.getRcn());
                    }
                    if (isNotEmpty(existingOrganisation.getName())) {
                        existingOrganisation.setName(organisation.getName());
                    }
                    if (isNotEmpty(existingOrganisation.getShortName())) {
                        existingOrganisation.setShortName(organisation.getShortName());
                    }
                    if (isNotEmpty(existingOrganisation.getFundingOrganisation())) {
                        existingOrganisation.setFundingOrganisation(organisation.getFundingOrganisation());
                    }
                    if (isNotEmpty(existingOrganisation.getFundingEU())) {
                        existingOrganisation.setFundingEU(organisation.getFundingEU());
                    }
                    if (isNotEmpty(existingOrganisation.getVatNumber())) {
                        existingOrganisation.setVatNumber(organisation.getVatNumber());
                    }
                    if (isNotEmpty(existingOrganisation.getNutsCode())) {
                        existingOrganisation.setNutsCode(organisation.getNutsCode());
                    }
                    if (isNotEmpty(existingOrganisation.getSme())) {
                        existingOrganisation.setSme(organisation.getSme());
                    }
                    if (isNotEmpty(existingOrganisation.getType())) {
                        existingOrganisation.setType(organisation.getType());
                    }
                    organisationRepository.save(existingOrganisation);
                } else {
                    organisationRepository.save(organisation);
                }

            }
        } catch (IOException | DataIntegrityViolationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private void updateContactInfos(Organisation organisation, Organisation existingOrganisation) {
        List<OrganisationContactInfo> contactInfos = organisation.getContactInfos();
        if (contactInfos == null || contactInfos.isEmpty()) {
            return;
        }
        if (existingOrganisation.getContactInfos() == null) {
            existingOrganisation.setContactInfos(contactInfos);
        } else {
            for (OrganisationContactInfo contactInfo : contactInfos) {
                if (existingOrganisation.getContactInfos().stream()
                        .noneMatch(ci -> ci.getType().equals(contactInfo.getType()) && ci.getValue().equals(contactInfo.getValue()))
                ) {
                    contactInfo.setOrganisation(existingOrganisation);
                    existingOrganisation.getContactInfos().add(contactInfo);
                }
            }
        }
    }

    private void updateCoordinates(Organisation organisation, Organisation existingOrganisation) {
        LocationCoordinates locationCoordinates = organisation.getLocationCoordinates();
        if (locationCoordinates == null) {
            return;
        }
        if (existingOrganisation.getLocationCoordinates() == null) {
            existingOrganisation.setLocationCoordinates(locationCoordinates);
        } else {
            if (isNotEmpty(locationCoordinates.getX())) {
                existingOrganisation.getLocationCoordinates().setX(locationCoordinates.getX());
            }
            if (isNotEmpty(locationCoordinates.getY())) {
                existingOrganisation.getLocationCoordinates().setY(locationCoordinates.getY());
            }
        }
    }

    private void updateAddress(Organisation organisation, Organisation existingOrganisation) {
        Address address = organisation.getAddress();
        if (address == null) {
            return;
        }
        if (existingOrganisation.getAddress() == null) {
            existingOrganisation.setAddress(address);
        } else {
            if (isNotEmpty(address.getStreet())) {
                existingOrganisation.getAddress().setStreet(address.getStreet());
            }
            if (isNotEmpty(address.getPostCode())) {
                existingOrganisation.getAddress().setPostCode(address.getPostCode());
            }
            if (isNotEmpty(address.getCity())) {
                existingOrganisation.getAddress().setCity(address.getCity());
            }
            if (isNotEmpty(address.getCountry())) {
                existingOrganisation.getAddress().setCountry(address.getCountry());
            }
        }
    }

    private static void updateProjectLinks(Organisation organisation, Organisation existingOrganisation) {
        for (OrganisationProjectJoin organisationProjectJoin : organisation.getOrganisationProjectJoins()) {
            if (existingOrganisation.getOrganisationProjectJoins().stream()
                    .noneMatch(opj -> opj.getProject().getReferenceId().equals(organisationProjectJoin.getProject().getReferenceId())
                    )) {
                organisationProjectJoin.setOrganisation(existingOrganisation);
                existingOrganisation.getOrganisationProjectJoins().add(organisationProjectJoin);
            } else {
                OrganisationProjectJoin existingOrganisationProjectJoin = existingOrganisation.getOrganisationProjectJoins().stream()
                        .filter(opj -> opj.getProject().getReferenceId().equals(organisationProjectJoin.getProject().getReferenceId()))
                        .findFirst()
                        .orElseThrow(() -> new OrganisationNotFoundException("OrganisationProjectJoin not found with referenceId: " + organisationProjectJoin.getProject().getReferenceId()));
                existingOrganisationProjectJoin.setType(organisationProjectJoin.getType());
            }
        }
    }

    private void setProjectLink(int PROJECT_ID_INDEX, int ROLE_INDEX, Row row, Organisation organisation) {
        long projectReferenceId = (long) row.getCell(PROJECT_ID_INDEX).getNumericCellValue();
        Project existingProject = projectRepository.findByReferenceId(projectReferenceId).orElseThrow(() -> new ProjectNotFoundException("Project not found with referenceId: " + projectReferenceId));
        OrganisationProjectJoinTypeEnum role = OrganisationProjectJoinTypeEnum.valueOfName(row.getCell(ROLE_INDEX).getStringCellValue());
        OrganisationProjectJoin projectLink = OrganisationProjectJoin.builder()
                .project(existingProject)
                .organisation(organisation)
                .type(role)
                .build();
        organisation.setOrganisationProjectJoins(List.of(projectLink));
    }

    private static String getBudget(int budgetIndex, Row row) {
        String budget;
        Cell budgetCell = row.getCell(budgetIndex);
        if (budgetCell.getCellType() == CellType.NUMERIC) {
            budget = String.valueOf(budgetCell.getNumericCellValue());
        } else {
            budget = budgetCell.getStringCellValue();
        }
        return budget;
    }

}
