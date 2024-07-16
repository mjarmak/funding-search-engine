package com.jeniustech.funding_search_engine.loader;

import com.jeniustech.funding_search_engine.constants.excel.OrganisationColumns;
import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.exceptions.OrganisationNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ProjectNotFoundException;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.jeniustech.funding_search_engine.loader.ProjectDataLoader.getFundingEU;
import static com.jeniustech.funding_search_engine.loader.ProjectDataLoader.getFundingOrganisation;
import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;
import static org.junit.jupiter.api.Assertions.fail;

@Transactional(rollbackFor = Exception.class)
@Rollback(false)
@SpringBootTest
public class OrganisationDataLoader {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    OrganisationRepository organisationRepository;

    public static final int BATCH_SIZE = 1000;

    int PROJECT_ID_INDEX = -1;
    int ORGANISATION_ID_INDEX = -1;
    int VAT_NUMBER_INDEX = -1;
    int NAME_INDEX = -1;
    int SHORT_NAME_INDEX = -1;
    int SME_INDEX = -1;
    int ACTIVITY_TYPE_INDEX = -1;
    int STREET_INDEX = -1;
    int POSTCODE_INDEX = -1;
    int CITY_INDEX = -1;
    int COUNTRY_INDEX = -1;
    int NUTS_CODE_INDEX = -1;
    int GEO_LOCATION_INDEX = -1;
    int ORGANIZATION_URL_INDEX = -1;
    int CONTACT_FORM_INDEX = -1;
    int CONTENT_UPDATE_DATE_INDEX = -1;
    int RCN_INDEX = -1;
    int ORDER_INDEX = -1;
    int ROLE_INDEX = -1;
    int NET_EC_CONTRIBUTION_INDEX = -1;
    int TOTAL_COST_INDEX = -1;

//    @Test
    void loadData() {
        IOUtils.setByteArrayMaxOverride(1_000_000_000);

        String path = "data/projects/split/";
        String fileName = "organization";
        int startFile = 79;
        int fileCount = 3;

        Stream.iterate(startFile, i -> i + 1).limit(fileCount)
                .forEach(i -> {
                    String excelFilePath = path + fileName + "_" + i + ".xlsx";
                    System.out.println("Loading file " + excelFilePath);
                    loadFile(excelFilePath);
                });
    }

    private void loadFile(String fileName) {
        try (
                FileInputStream fis = new FileInputStream(new ClassPathResource(fileName).getFile());
                Workbook workbook = new XSSFWorkbook(fis)
        ) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            // get headers
            var headers = sheet.getRow(0);
            for (Cell cell : headers) {
                switch (cell.getStringCellValue()) {
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
                    case OrganisationColumns.CONTENT_UPDATE_DATE -> CONTENT_UPDATE_DATE_INDEX = cell.getColumnIndex();
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
                            CONTENT_UPDATE_DATE_INDEX,
                            RCN_INDEX,
                            ORDER_INDEX,
                            ROLE_INDEX,
                            NET_EC_CONTRIBUTION_INDEX,
                            TOTAL_COST_INDEX
                    ).contains(-1)
            ) {
                System.out.println("Header not found");
                fail();
            }

            // save in batches of 1000
            List<Organisation> organisations = new ArrayList<>();
            for (Row row : sheet) {
//                System.out.println(row.getRowNum() + ", ");
                Organisation organisation = getOrganisation(row);
                processSave(sheet, organisations, row, organisation);
            }
        } catch (IOException | DataIntegrityViolationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private void processSave(Sheet sheet, List<Organisation> items, Row row, Organisation item) {
        if (item != null) {
            Optional<Organisation> existingItem = items.stream()
                    .filter(i -> i.getReferenceId().equals(item.getReferenceId()))
                    .findFirst();
            if (existingItem.isPresent()) {
                Organisation itemToUpdate = existingItem
                        .get();
                updateProjectLinks(item, itemToUpdate);
                updateContactInfos(item, itemToUpdate);
            } else {
                items.add(item);
            }
            if (items.size() == BATCH_SIZE) {
                logSaveBatch(items);
                save(items);
                items.clear();
            }
        } else if (!items.isEmpty()) {
            logSaveBatch(items);
            save(items);
            items.clear();
        }
        if (row.getRowNum() == sheet.getLastRowNum() && !items.isEmpty()) {
            logSaveBatch(items);
            save(items);
            items.clear();
        }
    }

    private static void logSaveBatch(List<Organisation> items) {
        System.out.println("Saving batch of " + items.size() + " items, less than " + BATCH_SIZE + " due to duplicate item skipping\n");
    }

    private Organisation getOrganisation(Row row) {
        if (row.getRowNum() == 0) {
            return null; // skip headers
        } else if (row.getCell(ORGANISATION_ID_INDEX) == null) {
            return null; // skip empty rows
        }

        Organisation organisation = Organisation.builder()
                .referenceId(Long.parseLong(row.getCell(ORGANISATION_ID_INDEX).getStringCellValue()))
                .vatNumber(row.getCell(VAT_NUMBER_INDEX).getStringCellValue())
                .name(row.getCell(NAME_INDEX).getStringCellValue())
                .shortName(row.getCell(SHORT_NAME_INDEX).getStringCellValue())
                .sme(BooleanEnum.fromBoolean(row.getCell(SME_INDEX).getStringCellValue()))
                .nutsCode(row.getCell(NUTS_CODE_INDEX).getStringCellValue())
                .rcn(row.getCell(RCN_INDEX).getStringCellValue())
                .type(OrganisationTypeEnum.of(row.getCell(ACTIVITY_TYPE_INDEX).getStringCellValue()))
                .build();

        // set project, organisationProjectJoins
        setProjectLink(
                PROJECT_ID_INDEX,
                row,
                organisation,
                OrganisationProjectJoinTypeEnum.valueOfName(row.getCell(ROLE_INDEX).getStringCellValue()),
                getFundingOrganisation(TOTAL_COST_INDEX, NET_EC_CONTRIBUTION_INDEX, row),
                getFundingEU(row, NET_EC_CONTRIBUTION_INDEX)
        );

        // set address
        setAddress(row, organisation);

        // set locationCoordinates
        setLocationCoordinates(row, organisation);

        // set contactInfo
        setContactInfo(row, organisation);

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
            return existingOrganisation;
        } else {
            return organisation;
        }
    }

    private void setContactInfo(Row row, Organisation organisation) {
        String url = row.getCell(ORGANIZATION_URL_INDEX).getStringCellValue();
        if (isNotEmpty(url)) {
            OrganisationContactInfo urlContactInfo = OrganisationContactInfo.builder()
                    .type(ContactInfoTypeEnum.URL)
                    .value(url)
                    .name("Website")
                    .organisation(organisation)
                    .build();
            organisation.setContactInfos(new ArrayList<>(List.of(urlContactInfo)));
        }
    }

    private void setLocationCoordinates(Row row, Organisation organisation) {
        String longitudeLatitude = row.getCell(GEO_LOCATION_INDEX).getStringCellValue();
        if (isNotEmpty(longitudeLatitude)) {
            String[] coordinates = longitudeLatitude.split(",");
            LocationCoordinates locationCoordinates = LocationCoordinates.builder()
                    .y(coordinates[0])
                    .x(coordinates[1])
                    .build();
            organisation.setLocationCoordinates(locationCoordinates);
        }
    }

    private void setAddress(Row row, Organisation organisation) {
        String street = row.getCell(STREET_INDEX).getStringCellValue();
        String postCode = row.getCell(POSTCODE_INDEX).getStringCellValue();
        String city = row.getCell(CITY_INDEX).getStringCellValue();
        String country = row.getCell(COUNTRY_INDEX).getStringCellValue();
        Address address = Address.builder()
                .street(street)
                .postCode(postCode)
                .city(city)
                .build();
        if (isNotEmpty(country)) {
            address.setCountry(CountryEnum.valueOf(country));
        }
        if (isNotEmpty(street) || isNotEmpty(postCode) || isNotEmpty(city) || isNotEmpty(country)) {
            organisation.setAddress(address);
        }
    }

    private void save(List<Organisation> organisations) {
        organisationRepository.saveAllAndFlush(organisations);
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

                if (isNotEmpty(existingOrganisationProjectJoin.getFundingOrganisation())) {
                    existingOrganisationProjectJoin.setFundingOrganisation(organisationProjectJoin.getFundingOrganisation());
                }
                if (isNotEmpty(existingOrganisationProjectJoin.getFundingEU())) {
                    existingOrganisationProjectJoin.setFundingEU(organisationProjectJoin.getFundingEU());
                }

            }
        }
    }

    private void setProjectLink(
            int PROJECT_ID_INDEX,
            Row row,
            Organisation organisation,
            OrganisationProjectJoinTypeEnum role,
            BigDecimal fundingOrganisation,
            BigDecimal fundingEU
    ) {
        long projectReferenceId = Long.parseLong(row.getCell(PROJECT_ID_INDEX).getStringCellValue());
        Project existingProject = projectRepository.findByReferenceId(projectReferenceId).orElseThrow(() -> new ProjectNotFoundException("Project not found with referenceId: " + projectReferenceId));
        OrganisationProjectJoin projectLink = OrganisationProjectJoin.builder()
                .project(existingProject)
                .organisation(organisation)
                .type(role)
                .fundingOrganisation(fundingOrganisation)
                .fundingEU(fundingEU)
                .build();
        organisation.setOrganisationProjectJoins(new ArrayList<>(List.of(projectLink)));
    }

}
