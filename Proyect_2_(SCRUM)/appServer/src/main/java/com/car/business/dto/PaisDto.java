package com.car.business.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaisDto extends BaseDto<String> {

    private String nombre;
    private List<String> provinciaIds;
}
