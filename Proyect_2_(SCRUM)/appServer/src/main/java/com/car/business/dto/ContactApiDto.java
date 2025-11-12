package com.car.business.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ContactApiDto {
    private String type;
    private String value;
    private String note;
}
