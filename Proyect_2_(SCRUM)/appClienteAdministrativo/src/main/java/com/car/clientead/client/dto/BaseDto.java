package com.car.clientead.client.dto;

import lombok.Data;

@Data
public class BaseDto<ID> {

    private ID id;
    private Boolean eliminado;
}



