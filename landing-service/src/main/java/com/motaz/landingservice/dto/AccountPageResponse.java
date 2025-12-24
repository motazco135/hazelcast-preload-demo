package com.motaz.landingservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Embedded _embedded;
    private Page page;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<AccountDto> accountDtoList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page implements Serializable {
        private static final long serialVersionUID = 1L;
        private int size;
        private int totalElements;
        private int totalPages;
        private int number;
    }
}
