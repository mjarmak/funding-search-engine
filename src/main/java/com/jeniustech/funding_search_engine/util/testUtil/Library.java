package com.jeniustech.funding_search_engine.util.testUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class Library {
    public String libraryname;
    public String testField;

    @JsonProperty("mymusic")
    public List<Song> songs;
}
