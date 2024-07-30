package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.EnumException;

public enum FundingSchemeEnum {
    HORIZON_AG("HORIZON-AG"),
    HORIZON_CSA("HORIZON-CSA"),
    HORIZON_JU_RIA("HORIZON-JU-RIA"),
    HORIZON_EIT_KIC("HORIZON-EIT-KIC"),
    HORIZON_SGA_AG("HORIZON-SGA-AG"),
    HORIZON_TMA_MSCA_PF_EF("HORIZON-TMA-MSCA-PF-EF"),
    HORIZON_AG_UN("HORIZON-AG-UN"),
    CSA("CSA"),
    MSCA_PF("MSCA-PF"),
    HORIZON_ERC("HORIZON-ERC"),
    HORIZON_JU_IA("HORIZON-JU-IA"),
    HORIZON_ERC_POC("HORIZON-ERC-POC"),
    HORIZON_IA("HORIZON-IA"),
    HORIZON_RIA("HORIZON-RIA"),
    HORIZON_TMA_MSCA_PF_GF("HORIZON-TMA-MSCA-PF-GF"),
    HORIZON_AG_LS("HORIZON-AG-LS"),
    HORIZON_TMA_MSCA_Cofund_D("HORIZON-TMA-MSCA-Cofund-D"),
    HORIZON_TMA_MSCA_Cofund_P("HORIZON-TMA-MSCA-Cofund-P"),
    IA("IA"),
    RIA("RIA"),
    HORIZON_JU_CSA("HORIZON-JU-CSA"),
    HORIZON_COFUND("HORIZON-COFUND"),
    HORIZON_EIC_ACC_BF("HORIZON-EIC-ACC-BF"),
    EIC_ACC("EIC-ACC"),
    HORIZON_EIC("HORIZON-EIC"),
    EIC("EIC"),
    ERC("ERC"),
    ERC_POC("ERC-POC"),
    HORIZON_ERC_SYG("HORIZON-ERC-SYG"),
    HORIZON_TMA_MSCA_DN_JD("HORIZON-TMA-MSCA-DN-JD"),
    HORIZON_TMA_MSCA_SE("HORIZON-TMA-MSCA-SE"),
    HORIZON_TMA_MSCA_DN("HORIZON-TMA-MSCA-DN"),
    EURATOM_CSA("EURATOM-CSA"),
    HORIZON_PCP("HORIZON-PCP"),
    HORIZON_TMA_MSCA_DN_ID("HORIZON-TMA-MSCA-DN-ID"),
    ERC_SYG("ERC-SYG"),
    EURATOM_IA("EURATOM-IA"),
    EURATOM_RIA("EURATOM-RIA"),
    MSCA_SE("MSCA-SE"),
    EURATOM_COFUND("EURATOM-COFUND"),
    MSCA_DN("MSCA-DN");

    private final String name;

    public String getName() {
        return name;
    }

    FundingSchemeEnum(final String name) {
        this.name = name;
    }

    public static FundingSchemeEnum valueOfName(String name) {
        for (FundingSchemeEnum fundingSchemeEnum : FundingSchemeEnum.values()) {
            if (fundingSchemeEnum.name.equals(name)) {
                return fundingSchemeEnum;
            }
        }
        throw new EnumException("FundingSchemeEnum not found for name: " + name);
    }

}
