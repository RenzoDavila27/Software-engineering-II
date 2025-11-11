package com.fioritech.car.bussiness.dto;

import lombok.Data;

@Data
public class BaseDto<ID> {

    private ID id;
    private Boolean eliminado;
}
