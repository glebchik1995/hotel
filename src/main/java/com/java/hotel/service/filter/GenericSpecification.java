package com.java.hotel.service.filter;

import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import static com.java.hotel.service.filter.Operation.NOT_NULL;
import static com.java.hotel.service.filter.Operation.NULL;

public class GenericSpecification<T> implements Specification<T> {
    private static final EnumSet<Operation> NULL_OPERATIONS = EnumSet.of(NULL, NOT_NULL);
    private static final Set<String> PRIMITIVE_NUMBERS = Set.of("byte", "short", "int", "long", "float", "double");
    private final CriteriaModel criteriaModel;
    private final Class<T> entityClass;

    public GenericSpecification(CriteriaModel criteriaModel, Class<T> entityClass) {
        this.criteriaModel = criteriaModel;
        this.entityClass = entityClass;
        checkCriteria(criteriaModel);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Operation operation = criteriaModel.getOperation();
        String fieldName = criteriaModel.getField();
        Path<Object> expression = root.get(fieldName);
        Object value = criteriaModel.getValue();
        switch (operation) {
            case NULL:
                return criteriaBuilder.isNull(expression);
            case NOT_NULL:
                return criteriaBuilder.isNotNull(expression);
            case EQ:
                return criteriaBuilder.equal(expression, value);
            case LIKE:
                if (isString(fieldName)) {
                    String likeString = "%" + value + "%";
                    return criteriaBuilder.like(expression.as(String.class), likeString);
                }
                break;
            case GT:
                if (isNumber(fieldName)) {
                    return criteriaBuilder.gt(expression.as(BigDecimal.class), new BigDecimal(String.valueOf(value)));
                } else if (isDate(fieldName)) {
                    return criteriaBuilder.greaterThan(expression.as(LocalDateTime.class), toDate(value));
                }
                break;
            case LT:
                if (isNumber(fieldName)) {
                    return criteriaBuilder.lt(expression.as(BigDecimal.class), new BigDecimal(String.valueOf(value)));
                } else if (isDate(fieldName)) {
                    return criteriaBuilder.lessThan(expression.as(LocalDateTime.class), toDate(value));
                }
                break;
        }
        return null;
    }

    private void checkCriteria(CriteriaModel criteriaModel) {
        if (criteriaModel == null) {
            throw new IllegalArgumentException("CriteriaModel не должен быть null");
        }

        if (StringUtils.isBlank(criteriaModel.getField())) {
            throw new IllegalArgumentException("Поле должно быть заполнено!");
        }

        Operation operation = criteriaModel.getOperation();
        if (operation == null) {
            throw new IllegalArgumentException("Операция не должна быть null");
        }

        if (!NULL_OPERATIONS.contains(operation) && criteriaModel.getValue() == null) {
            throw new IllegalArgumentException("Значение не должно быть null");
        }
    }

    private LocalDateTime toDate(Object value) {
        return switch (value) {
            case LocalDateTime localDateTime -> localDateTime;
            case LocalDate date -> LocalDateTime.of(date, LocalTime.MIN);
            case Date date -> LocalDateTime.ofEpochSecond(date.getTime(), 0, ZoneOffset.UTC);
            case String s -> LocalDateTime.parse(s);
            case null, default -> throw new RuntimeException("Неподдерживаемый тип даты: " + value);
        };
    }

    private boolean isNumber(String fieldName) {
        Class<?> fieldType = getFieldType(fieldName);
        if (fieldType.isPrimitive()) {
            return PRIMITIVE_NUMBERS.contains(fieldType.getName());
        }
        return Number.class.isAssignableFrom(fieldType);
    }

    private boolean isString(String fieldName) {
        Class<?> fieldType = getFieldType(fieldName);
        return fieldType.isEnum() || String.class.equals(fieldType);
    }

    private boolean isDate(String fieldName) {
        Class<?> fieldType = getFieldType(fieldName);
        return LocalDate.class.equals(fieldType) || LocalDateTime.class.equals(fieldType) || Date.class.isAssignableFrom(fieldType);
    }

    private Class<?> getFieldType(String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            return field.getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
