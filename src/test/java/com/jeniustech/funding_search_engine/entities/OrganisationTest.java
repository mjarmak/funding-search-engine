package com.jeniustech.funding_search_engine.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganisationTest {

    @Test
    void isDifferentTest_1() {
        Organisation organisation1 = new Organisation();
        Organisation organisation2 = new Organisation();
        organisation1.setId(1L);
        organisation2.setId(2L);
        assertTrue(organisation1.isDifferent(organisation2));
    }

    @Test
    void isDifferentTest_2() {
        Organisation organisation1 = new Organisation();
        Organisation organisation2 = new Organisation();
        organisation1.setId(1L);
        organisation2.setId(1L);
        assertFalse(organisation1.isDifferent(organisation2));
    }

    @Test
    void isDifferentTest_3() {
        Organisation organisation1 = new Organisation();
        Organisation organisation2 = new Organisation();
        organisation1.setId(null);
        organisation2.setId(1L);
        assertFalse(organisation1.isDifferent(organisation2));
    }

    @Test
    void isDifferentTest_4() {
        assertFalse(Organisation.builder()
                .referenceId("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .referenceId("test")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_5() {
        assertFalse(Organisation.builder()
                .referenceId("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .referenceId("null")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_6() {
        assertTrue(Organisation.builder()
                .referenceId("test")
                .vatNumber("test")
                .name("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .referenceId("test")
                                .vatNumber("nope")
                                .name("test")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_7() {
        assertFalse(Organisation.builder()
                .referenceId("test")
                .vatNumber("test")
                .name("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .referenceId("nope")
                                .vatNumber("test")
                                .name("nope")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_8() {
        assertFalse(Organisation.builder()
                .name("test")
                .vatNumber("test")
                .name("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .name("nope")
                                .vatNumber("test")
                                .name("nope")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_9() {
        assertFalse(Organisation.builder()
                .name("test")
                .referenceId("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .name("nope")
                                .referenceId("test")
                                .build()
                )
        );
    }

    @Test
    void isDifferentTest_10() {
        assertTrue(Organisation.builder()
                .name("test")
                .shortName("test")
                .build()
                .isDifferent(
                        Organisation.builder()
                                .name("nope")
                                .shortName("test")
                                .build()
                )
        );
    }

}
