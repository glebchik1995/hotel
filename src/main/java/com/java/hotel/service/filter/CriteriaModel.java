package com.java.hotel.service.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaModel implements Serializable {

    private String field;

    private Operation operation;

    private Object value;
}
