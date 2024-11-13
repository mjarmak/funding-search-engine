package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CountryDTO;
import com.jeniustech.funding_search_engine.enums.CountryEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/util")
public class UtilController {

    @GetMapping("/countries")
    public List<CountryDTO> getCountries() {
        return Arrays.stream(CountryEnum.values()).map(
                countryEnum -> CountryDTO.builder()
                        .code(countryEnum.getName())
                        .name(countryEnum.getDisplayName())
                        .id(countryEnum.getHierarchy())
                        .build()
        )
                .sorted(Comparator.comparing(CountryDTO::getCode))
                .toList();
    }

}
