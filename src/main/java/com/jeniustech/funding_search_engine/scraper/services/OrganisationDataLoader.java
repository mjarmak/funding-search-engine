package com.jeniustech.funding_search_engine.scraper.services;

import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.exceptions.OrganisationNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ProjectNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.scraper.constants.excel.OrganisationCSVColumns;
import com.jeniustech.funding_search_engine.scraper.util.CSVSplitter;
import com.jeniustech.funding_search_engine.services.CSVService;
import com.jeniustech.funding_search_engine.services.solr.PartnerSolrClientService;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.services.CSVService.DELIMITER_DEFAULT;
import static com.jeniustech.funding_search_engine.services.CSVService.DELIMITER_TAB;
import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;
import static com.jeniustech.funding_search_engine.util.StringUtil.valueOrDefault;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganisationDataLoader {

    private final ProjectRepository projectRepository;
    private final PartnerSolrClientService partnerSolrClientService;
    private final OrganisationRepository organisationRepository;
    private final CSVService csvService;

    public static final int BATCH_SIZE = 1000;
    private int duplicates = 0;
    private int total = 0;
    private int problems = 0;
    private String[] headers;
    private List<String[]> problemRows = new ArrayList<>();

    int PROJECT_ID_INDEX = -1;
    int PROJECT_RCN_INDEX = -1;
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
    int RCN_INDEX = -1;
    int ROLE_INDEX = -1;
    int NET_EC_CONTRIBUTION_INDEX = -1;
    int EC_CONTRIBUTION_INDEX = -1;
    int TOTAL_COST_INDEX = -1;

    public void splitFileAndLoadData(String fileName, boolean oldFormat, boolean skipUpdate, int rowsPerFile, boolean onlyValidate) {
        duplicates = 0;
        total = 0;
        problems = 0;
        problemRows.clear();
        resetIndexes();

        fileName = csvService.preprocessCSV(fileName, oldFormat);

        List<String> splitFileNames = CSVSplitter.splitCSVFile(fileName, rowsPerFile);

        for (String splitFileName : splitFileNames) {
            log.info("Loading data from " + splitFileName);
            loadData(splitFileName, oldFormat, skipUpdate, onlyValidate);
        }
        if (onlyValidate && problems > 0) {
            log.error("Writing problems to file");
            csvService.writeCSV(headers, problemRows, fileName.replace(".csv", "_invalid_fields.csv"));
        }
    }

    public void loadSolrData() {
        log.info("Loading organisations to solr");
        // do in batch of 1000
        int pageNumber = 0;
        int pageSize = 1000;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Organisation> organisations = organisationRepository.findAll(pageable).getContent();
        while (!organisations.isEmpty()) {
            log.info("Saving batch of " + organisations.size() + " items");
            partnerSolrClientService.add(SolrMapper.mapPartnersToSolrDocument(organisations), 100_000);
            pageNumber++;
            organisations = organisationRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
        }
    }

    private void loadData(String fileName, boolean oldFormat, boolean skipUpdate, boolean onlyValidate) {

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(oldFormat ? DELIMITER_TAB : DELIMITER_DEFAULT)
                        .withQuoteChar(CSVService.QUOTE)
                        .withEscapeChar('\\')
                        .build()
                ).build()
        ) {
            headers = reader.readNext();
            int index = 0;
            for (String cell : headers) {
                switch (cell) {
                    case OrganisationCSVColumns.PROJECT_ID -> PROJECT_ID_INDEX = index;
                    case OrganisationCSVColumns.PROJECT_RCN -> PROJECT_RCN_INDEX = index;
                    case OrganisationCSVColumns.ORGANISATION_ID, OrganisationCSVColumns.ID -> ORGANISATION_ID_INDEX = index;
                    case OrganisationCSVColumns.VAT_NUMBER -> VAT_NUMBER_INDEX = index;
                    case OrganisationCSVColumns.NAME -> NAME_INDEX = index;
                    case OrganisationCSVColumns.SHORT_NAME -> SHORT_NAME_INDEX = index;
                    case OrganisationCSVColumns.SME -> SME_INDEX = index;
                    case OrganisationCSVColumns.ACTIVITY_TYPE -> ACTIVITY_TYPE_INDEX = index;
                    case OrganisationCSVColumns.STREET -> STREET_INDEX = index;
                    case OrganisationCSVColumns.POSTCODE -> POSTCODE_INDEX = index;
                    case OrganisationCSVColumns.CITY -> CITY_INDEX = index;
                    case OrganisationCSVColumns.COUNTRY -> COUNTRY_INDEX = index;
                    case OrganisationCSVColumns.NUTS_CODE -> NUTS_CODE_INDEX = index;
                    case OrganisationCSVColumns.GEO_LOCATION -> GEO_LOCATION_INDEX = index;
                    case OrganisationCSVColumns.ORGANIZATION_URL, OrganisationCSVColumns.ORGANIZATION_URL_2 -> ORGANIZATION_URL_INDEX = index;
                    case OrganisationCSVColumns.CONTACT_FORM -> CONTACT_FORM_INDEX = index;
                    case OrganisationCSVColumns.RCN -> RCN_INDEX = index;
                    case OrganisationCSVColumns.ROLE -> ROLE_INDEX = index;
                    case OrganisationCSVColumns.EC_CONTRIBUTION -> EC_CONTRIBUTION_INDEX = index;
                    case OrganisationCSVColumns.NET_EC_CONTRIBUTION -> NET_EC_CONTRIBUTION_INDEX = index;
                    case OrganisationCSVColumns.TOTAL_COST -> TOTAL_COST_INDEX = index;
                }
                index++;
            }

            final List<Integer> headerIndexes = new ArrayList<>(List.of(
                    PROJECT_ID_INDEX,
                    VAT_NUMBER_INDEX,
                    NAME_INDEX,
                    SHORT_NAME_INDEX,
                    ACTIVITY_TYPE_INDEX,
                    STREET_INDEX,
                    POSTCODE_INDEX,
                    CITY_INDEX,
                    COUNTRY_INDEX,
                    ORGANIZATION_URL_INDEX,
                    CONTACT_FORM_INDEX,
                    ROLE_INDEX,
                    EC_CONTRIBUTION_INDEX
            ));
            if (!oldFormat) {
                headerIndexes.add(ORGANISATION_ID_INDEX);
                headerIndexes.add(SME_INDEX);
                headerIndexes.add(GEO_LOCATION_INDEX);
                headerIndexes.add(NUTS_CODE_INDEX);
                headerIndexes.add(NET_EC_CONTRIBUTION_INDEX);
                headerIndexes.add(TOTAL_COST_INDEX);
                headerIndexes.add(RCN_INDEX);
            }
            if (
                    headerIndexes.contains(-1)
            ) {
                log.error("Header not found, " + headerIndexes.stream().filter(i -> i == -1).map(String::valueOf).count() + " missing");
                throw new ScraperException("Header not found");
            }

            // save in batches of 1000
            List<Organisation> organisations = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Organisation organisation = getOrganisation(row, oldFormat, skipUpdate, onlyValidate);
                if (!onlyValidate) {
                    processSave(organisations, organisation, fileName);
                } else {
                    validateFields(organisation);
                    total++;
                    if (total % 1000 == 0) {
                        log.info("Validated " + total + " items");
                    }
                }
            }
            if (!onlyValidate) {
                log.info("Saving last batch of " + organisations.size() + " items");
                save(organisations, fileName);
            }
            log.info("Total: " + total + ", problems: " + problems);
        } catch (IOException | DataIntegrityViolationException | CsvValidationException e) {
            log.error(e.getMessage());
            throw new ScraperException(e.getMessage());
        }
    }

    private void validateFields(Organisation organisation) {
        if (organisation == null) {
            return;
        }
        if (!organisation.isFieldsValid()) {
            problems++;
            log.error("Invalid fields for project: " + organisation.getReferenceId() + " " + organisation.getName());
        }
    }

    private void resetIndexes() {
        PROJECT_ID_INDEX = -1;
        ORGANISATION_ID_INDEX = -1;
        VAT_NUMBER_INDEX = -1;
        NAME_INDEX = -1;
        SHORT_NAME_INDEX = -1;
        SME_INDEX = -1;
        ACTIVITY_TYPE_INDEX = -1;
        STREET_INDEX = -1;
        POSTCODE_INDEX = -1;
        CITY_INDEX = -1;
        COUNTRY_INDEX = -1;
        NUTS_CODE_INDEX = -1;
        GEO_LOCATION_INDEX = -1;
        ORGANIZATION_URL_INDEX = -1;
        CONTACT_FORM_INDEX = -1;
        RCN_INDEX = -1;
        ROLE_INDEX = -1;
        NET_EC_CONTRIBUTION_INDEX = -1;
        EC_CONTRIBUTION_INDEX = -1;
        TOTAL_COST_INDEX = -1;
    }

    private void processSave(List<Organisation> items, Organisation item, String fileName) {
        total++;
        if (item != null) {
            Optional<Organisation> existingItem = items.stream()
                    .filter(i -> !i.isDifferent(item))
                    .findFirst();
            if (existingItem.isPresent()) {
                Organisation itemToUpdate = existingItem
                        .get();
                updateProjectLinks(item, itemToUpdate);
                updateContactInfos(item, itemToUpdate);
                duplicates++;
            } else {
                items.add(item);
            }
            if (items.size() == BATCH_SIZE) {
                log.info("Saving batch of " + items.size() + " items, duplicates: " + duplicates + ", total: " + total);
                save(items, fileName);
                items.clear();
                duplicates = 0;
            }
        } else if (!items.isEmpty()) {
            log.info("Saving batch of " + items.size() + " items, duplicates: " + duplicates + ", total: " + total);
            save(items, fileName);
            items.clear();
        }
    }

    private Organisation getOrganisation(String[] row, boolean oldFormat, boolean skipUpdate, boolean onlyValidate) {
        if (row[NAME_INDEX] == null) {
            return null; // skip empty rows
        }

        Organisation organisation = Organisation.builder()
                .referenceId(ORGANISATION_ID_INDEX == -1 ? null : valueOrDefault(row[ORGANISATION_ID_INDEX], null))
                .vatNumber(valueOrDefault(row[VAT_NUMBER_INDEX], null))
                .name(valueOrDefault(row[NAME_INDEX], null))
                .shortName(valueOrDefault(row[SHORT_NAME_INDEX], null))
                .sme(SME_INDEX == -1 ? null : BooleanEnum.fromBoolean(row[SME_INDEX]))
                .nutsCode(NUTS_CODE_INDEX == -1 ? null : valueOrDefault(row[NUTS_CODE_INDEX], null))
                .rcn(RCN_INDEX == -1 ? null : valueOrDefault(row[RCN_INDEX], null))
                .type(OrganisationTypeEnum.of(row[ACTIVITY_TYPE_INDEX]))
                .build();

        // set project, organisationProjectJoins
        if (!onlyValidate) {
            setProjectLink(
                    row,
                    organisation,
                    OrganisationProjectJoinTypeEnum.valueOfName(row[ROLE_INDEX]),
                    getFundingOrganisation(TOTAL_COST_INDEX, NET_EC_CONTRIBUTION_INDEX, EC_CONTRIBUTION_INDEX, row),
                    getBudget(row, NET_EC_CONTRIBUTION_INDEX, EC_CONTRIBUTION_INDEX)
            );
        }

        // set address
        setAddress(row, organisation);

        // set locationCoordinates
        setLocationCoordinates(row, organisation);

        // set contactInfo
        setContactInfo(row, organisation);

        if (onlyValidate) {
            return organisation;
        }

        Optional<Organisation> existingOrganisationOptional = findOrganisation(organisation);
        if (existingOrganisationOptional.isPresent()) {
            Organisation existingOrganisation = existingOrganisationOptional.get();

            updateProjectLinks(organisation, existingOrganisation);

            if (skipUpdate) {
                return existingOrganisation;
            }

            if (!oldFormat) {
                updateAddress(organisation, existingOrganisation);
                updateContactInfos(organisation, existingOrganisation);
                updateCoordinates(organisation, existingOrganisation);
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getReferenceId())) && isNotEmpty(organisation.getReferenceId())) {
                existingOrganisation.setReferenceId(organisation.getReferenceId());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getRcn())) && isNotEmpty(organisation.getRcn())) {
                existingOrganisation.setRcn(organisation.getRcn());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getName())) && isNotEmpty(organisation.getName())) {
                existingOrganisation.setName(organisation.getName());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getShortName())) && isNotEmpty(organisation.getShortName())) {
                existingOrganisation.setShortName(organisation.getShortName());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getVatNumber())) && isNotEmpty(organisation.getVatNumber())) {
                existingOrganisation.setVatNumber(organisation.getVatNumber());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getNutsCode())) && isNotEmpty(organisation.getNutsCode())) {
                existingOrganisation.setNutsCode(organisation.getNutsCode());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getSme())) && isNotEmpty(organisation.getSme())) {
                existingOrganisation.setSme(organisation.getSme());
            }
            if ((!oldFormat || !isNotEmpty(existingOrganisation.getType())) && isNotEmpty(organisation.getType())) {
                existingOrganisation.setType(organisation.getType());
            }
            existingOrganisation.setUpdatedAt(DateMapper.map(LocalDateTime.now()));
            return existingOrganisation;
        } else {
            return organisation;
        }
    }

    private Optional<Organisation> findOrganisation(Organisation organisation) {
        Optional<Organisation> organisationOptional = Optional.empty();
        if (organisation.getReferenceId() != null && !organisation.getReferenceId().isBlank()) {
            try {
                organisationOptional = organisationRepository.findByReferenceId(organisation.getReferenceId());
            } catch (Exception e) {
                log.error("Error finding organisation for referenceId: " + organisation.getReferenceId());
                log.error(e.getMessage());
                throw new ScraperException(e.getMessage());
            }
        }
        if (organisationOptional.isEmpty() && organisation.getVatNumber() != null && !organisation.getVatNumber().isBlank()) {
            try {
                organisationOptional = organisationRepository.findByVatNumber(organisation.getVatNumber());
            } catch (Exception e) {
                log.error("Error finding organisation for vatNumber: " + organisation.getVatNumber());
                log.error(e.getMessage());
                throw new ScraperException(e.getMessage());
            }
        }
        if (organisationOptional.isEmpty() && organisation.getShortName() != null && !organisation.getShortName().isBlank()) {
            try {
                List<Organisation> result = organisationRepository.findByShortName(organisation.getShortName());
                if (result.size() == 1) {
                    organisationOptional = Optional.of(result.get(0));
                    if (organisation.isDifferent(organisationOptional.get())
                    ) {
                        organisationOptional = Optional.empty();
                    }
                }
            } catch (Exception e) {
                log.error("Error finding organisation for shortName: " + organisation.getShortName());
                log.error(e.getMessage());
                throw new ScraperException(e.getMessage());
            }
        }
        if (organisationOptional.isEmpty() && organisation.getName() != null && !organisation.getName().isBlank()) {
            try {
                List<Organisation> result = organisationRepository.findByName(organisation.getName());
                if (result.size() == 1) {
                    organisationOptional = Optional.of(result.get(0));
                    if (organisation.isDifferent(organisationOptional.get())
                    ) {
                        organisationOptional = Optional.empty();
                    }
                }
            } catch (Exception e) {
                log.error("Error finding organisation for name: " + organisation.getName());
                log.error(e.getMessage());
                throw new ScraperException(e.getMessage());
            }
        }
        return organisationOptional;
    }

    private void setContactInfo(String[] row, Organisation organisation) {
        String url = row[ORGANIZATION_URL_INDEX];
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

    private void setLocationCoordinates(String[] row, Organisation organisation) {
        if (GEO_LOCATION_INDEX == -1) {
            return;
        }
        String longitudeLatitude = row[GEO_LOCATION_INDEX];
        if (isNotEmpty(longitudeLatitude)) {
            String[] coordinates = longitudeLatitude.split(",");
            LocationCoordinates locationCoordinates = LocationCoordinates.builder()
                    .y(coordinates[0])
                    .x(coordinates[1])
                    .build();
            organisation.setLocationCoordinates(locationCoordinates);
        }
    }

    private void setAddress(String[] row, Organisation organisation) {
        String street = row[STREET_INDEX];
        String postCode = row[POSTCODE_INDEX];
        String city = row[CITY_INDEX];
        String country = row[COUNTRY_INDEX];
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

    private void save(List<Organisation> organisations, String fileName) {
        if (organisations.isEmpty()) {
            return;
        }
        try{
            organisationRepository.saveAll(organisations);
        } catch (Exception e) {
            log.error(e.getMessage());
//            e.printStackTrace();
            csvService.writePartnersCSV(organisations, fileName);
        }
    }

    private void updateContactInfos(Organisation organisation, Organisation existingOrganisation) {
        List<OrganisationContactInfo> contactInfos = organisation.getContactInfos();
        if (contactInfos == null || contactInfos.isEmpty()) {
            return;
        }
        if (existingOrganisation.getContactInfos() == null) {
            existingOrganisation.setContactInfos(new ArrayList<>());
        }
        for (OrganisationContactInfo contactInfo : contactInfos) {
            if (existingOrganisation.getContactInfos().stream()
                    .noneMatch(ci -> ci.getType().equals(contactInfo.getType()) && ci.getValue().equals(contactInfo.getValue()))
            ) {
                contactInfo.setOrganisation(existingOrganisation);
                existingOrganisation.getContactInfos().add(contactInfo);
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
        if (organisation.getOrganisationProjectJoins() == null) {
            organisation.setOrganisationProjectJoins(new ArrayList<>());
        }
        for (OrganisationProjectJoin organisationProjectJoin : organisation.getOrganisationProjectJoins()) {
            if (existingOrganisation.getOrganisationProjectJoins() == null) {
                existingOrganisation.setOrganisationProjectJoins(new ArrayList<>());
            }
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
            String[] row,
            Organisation organisation,
            OrganisationProjectJoinTypeEnum role,
            BigDecimal fundingOrganisation,
            BigDecimal fundingEU
    ) {
        if (role == null) {
            role = OrganisationProjectJoinTypeEnum.UNKNOWN;
        }
        Project existingProject = findProject(row);
        OrganisationProjectJoin projectLink = OrganisationProjectJoin.builder()
                .project(existingProject)
                .organisation(organisation)
                .type(role)
                .fundingOrganisation(fundingOrganisation)
                .fundingEU(fundingEU)
                .build();
        organisation.setOrganisationProjectJoins(new ArrayList<>(List.of(projectLink)));
    }

    private Project findProject(String[] row) {
        String referenceId = row[PROJECT_ID_INDEX];
        Optional<Project> existingProject = projectRepository.findByReferenceId(referenceId);

        String projectRcn = PROJECT_RCN_INDEX == -1 ? null : row[PROJECT_RCN_INDEX];
        if (existingProject.isEmpty() && projectRcn != null) {
            existingProject = projectRepository.findByRcn(projectRcn);
        }
        return existingProject.orElseThrow(() -> new ProjectNotFoundException("Project not found with referenceId: " + referenceId + " or rcn: " + projectRcn));
    }

    public static BigDecimal getFundingOrganisation(int TOTAL_COST_INDEX, int NET_EC_CONTRIBUTION_INDEX, int EC_CONTRIBUTION_INDEX, String[] row) {
        BigDecimal totalCost = new BigDecimal(getBudgetString(TOTAL_COST_INDEX, row));

        BigDecimal result = getEcContribution(row, NET_EC_CONTRIBUTION_INDEX, EC_CONTRIBUTION_INDEX);

        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else if (result == null || result.compareTo(totalCost) == 0) {
            return null;
        } else {
            return totalCost.subtract(result).stripTrailingZeros();
        }
    }

    public static BigDecimal getBudget(String[] row, int NET_EC_CONTRIBUTION_INDEX, int EC_CONTRIBUTION_INDEX) {
        BigDecimal budget = getEcContribution(row, NET_EC_CONTRIBUTION_INDEX, EC_CONTRIBUTION_INDEX);

        if (budget == null ||budget.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return budget;
    }

    @Nullable
    private static BigDecimal getEcContribution(String[] row, int NET_EC_CONTRIBUTION_INDEX, int EC_CONTRIBUTION_INDEX) {
        if (NET_EC_CONTRIBUTION_INDEX != -1) {
            return new BigDecimal(getBudgetString(NET_EC_CONTRIBUTION_INDEX, row));
        } else if (EC_CONTRIBUTION_INDEX != -1) {
            return new BigDecimal(getBudgetString(EC_CONTRIBUTION_INDEX, row));
        } else {
            return null;
        }
    }

    public static String getBudgetString(int budgetIndex, String[] row) {
        if (budgetIndex == -1) {
            return "0";
        }

        String budget;
        String budgetCell = row[budgetIndex];
        if (!isNotEmpty(budgetCell)) {
            return "0";
        }
        budget = budgetCell
                .replace(" ", "")
                .replace(",", ".")
                .trim();
        return budget;
    }
}
