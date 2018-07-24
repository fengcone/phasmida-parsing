package com.jd.presort.parsing.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnalyzerResponse {
    private String provinceName;
    private String cityName;
    private String countyName;
    private String townName;
    private Integer provinceId;
    private Integer cityId;
    private Integer countyId;
    private Integer townId;
    private String address;
}
