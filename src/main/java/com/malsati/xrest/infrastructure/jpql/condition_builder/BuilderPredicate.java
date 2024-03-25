package com.malsati.xrest.infrastructure.jpql.condition_builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface BuilderPredicate<T> {
    Predicate execute(Root<T> root,
                      String operator,
                      String fieldName,
                      Object value,
                      CriteriaBuilder criteriaBuilder);
}