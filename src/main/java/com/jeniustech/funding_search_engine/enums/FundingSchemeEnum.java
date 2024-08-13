package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.EnumException;

// =UNIQUE(O:O)
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
    MSCA_DN("MSCA-DN"),
    SME_1("SME-1"),
    SME("SME"),
    SME_2("SME-2"),
    MSCA_IF("MSCA-IF"),
    MSCA_IF_EF_RI("MSCA-IF-EF-RI"),
    SME_2b("SME-2b"),
    CSA_LSP("CSA-LSP"),
    MSCA_IF_EF_ST("MSCA-IF-EF-ST"),
    ERC_STG("ERC-STG"),
    ERC_ADG("ERC-ADG"),
    ERC_COG("ERC-COG"),
    ERC_POC_LS("ERC-POC-LS"),
    ERC_LVG("ERC-LVG"),
    ERC_SyG("ERC-SyG"),
    PCP("PCP"),
    MSCA_COFUND("MSCA-COFUND"),
    MSCA_IF_GF("MSCA-IF-GF"),
    MSCA_COFUND_DP("MSCA-COFUND-DP"),
    MSCA_COFUND_FP("MSCA-COFUND-FP"),
    MSCA_IF_EF_SE("MSCA-IF-EF-SE"),
    MSCA_IF_EF_CAR("MSCA-IF-EF-CAR"),
    MSCA_ITN_EID("MSCA-ITN-EID"),
    MSCA_ITN("MSCA-ITN"),
    PPI("PPI"),
    IMI2_RIA("IMI2-RIA"),
    CS2_RIA("CS2-RIA"),
    CS2_IA("CS2-IA"),
    SESAR_IA("SESAR-IA"),
    SESAR_RIA("SESAR-RIA"),
    Shift2Rail_RIA("Shift2Rail-RIA"),
    MSCA_ITN_EJD("MSCA-ITN-EJD"),
    Shift2Rail_RIA_LS("Shift2Rail-RIA-LS"),
    MSCA_RISE("MSCA-RISE"),
    MSCA_ITN_ETN("MSCA-ITN-ETN"),
    ERA_NET_Cofund("ERA-NET-Cofund"),
    ECSEL_IA("ECSEL-IA"),
    BBI_CSA("BBI-CSA"),
    BBI_RIA("BBI-RIA"),
    BBI_IA_DEMO("BBI-IA-DEMO"),
    H2020_EEN_SGA("H2020-EEN-SGA"),
    ECSEL_RIA("ECSEL-RIA"),
    SESAR_CSA("SESAR-CSA"),
    FCH2_CSA("FCH2-CSA"),
    CS2_CSA("CS2-CSA"),
    IA_LS("IA-LS"),
    COFUND_PCP("COFUND-PCP"),
    ECSEL_CSA("ECSEL-CSA"),
    BBI_IA_FLAG("BBI-IA-FLAG"),
    Shift2Rail_CSA("Shift2Rail-CSA"),
    FCH2_RIA("FCH2-RIA"),
    COFUND_EJP("COFUND-EJP"),
    FCH2_IA("FCH2-IA"),
    EuroHPC_IA("EuroHPC-IA"),
    Shift2Rail_IA("Shift2Rail-IA"),
    IMI2_CSA("IMI2-CSA"),
    COFUND("COFUND"),
    CSA_LS("CSA-LS");


    private final String name;

    public String getName() {
        return name;
    }

    FundingSchemeEnum(final String name) {
        this.name = name;
    }

    public static FundingSchemeEnum valueOfName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        for (FundingSchemeEnum fundingSchemeEnum : FundingSchemeEnum.values()) {
            if (fundingSchemeEnum.name.equals(name)) {
                return fundingSchemeEnum;
            }
        }
        throw new EnumException("FundingSchemeEnum not found for name: " + name);
    }

}
