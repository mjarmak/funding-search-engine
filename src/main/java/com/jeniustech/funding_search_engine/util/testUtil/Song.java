package com.jeniustech.funding_search_engine.util.testUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Song {
    public String artistName;
    @JsonProperty("Song Name") public String songName;
}
