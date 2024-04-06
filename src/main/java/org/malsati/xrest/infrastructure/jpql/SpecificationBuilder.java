package org.malsati.xrest.infrastructure.jpql;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.malsati.xrest.infrastructure.jpql.condition_builder.BinaryOperators;
import org.malsati.xrest.infrastructure.jpql.condition_builder.BuilderPredicate;
import org.malsati.xrest.infrastructure.jpql.condition_builder.MainTreeKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;

@Component
public class SpecificationBuilder<T> {
    public SpecificationBuilder() {
        binaryOperatorsMap.put(BinaryOperators.equal, this::isEqualPredicate);
        binaryOperatorsMap.put(BinaryOperators.notEqual, this::isNotEqualPredicate);
        binaryOperatorsMap.put(BinaryOperators.lessThan, this::isLessPredicate);
        binaryOperatorsMap.put(BinaryOperators.lessOrEqual, this::isLessOrEqualPredicate);

        binaryOperatorsMap.put(BinaryOperators.greaterThan, this::isGreaterPredicate);
        binaryOperatorsMap.put(BinaryOperators.greaterOrEqual, this::isGreaterOrEqualPredicate);
        binaryOperatorsMap.put(BinaryOperators.like, this::isLikePredicate);
    }

    ObjectMapper jsonParser = new ObjectMapper();

    public Specification<T> build(String whereCondition) {
        try {
            JsonNode rootNode = jsonParser.readTree(whereCondition);
            return buildSpecificationList(rootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Specification<T> buildWithAnd(String condition1, String condition2) {
        Specification<T> condition1Specification = build(condition1);
        Specification<T> condition2Specification = build(condition2);
        return (root, query, builder) -> {
            Predicate lhsPredicate = condition1Specification.toPredicate(root, query, builder);
            Predicate rhsPredicate = condition2Specification.toPredicate(root, query, builder);
            return builder.and(lhsPredicate, rhsPredicate);
        };
    }

    private Specification<T> buildSpecificationList(JsonNode node) {
        String operator = node.get(MainTreeKeys.operator).asText();
        if (isLeaf(operator)) {
            if (isTernaryOperator(operator)) {
                return (root, query, builder) -> buildPredicateForTernaryOperator(root, node, builder);
            }

            return (root, query, builder) -> buildPredicateForBinaryOperator(root, node, builder);
        }

        JsonNode lhs = node.get(MainTreeKeys.lhs);
        JsonNode rhs = node.get(MainTreeKeys.rhs);

        // Code for internal nodes
        Specification<T> lhsSpecification = buildSpecificationList(lhs);
        Specification<T> rhsSpecification = buildSpecificationList(rhs);
        return (root, query, builder) -> {
            Predicate lhsPredicate = lhsSpecification.toPredicate(root, query, builder);
            Predicate rhsPredicate = rhsSpecification.toPredicate(root, query, builder);
            if (operator.equalsIgnoreCase("&&")) {
                return builder.and(lhsPredicate, rhsPredicate);
            }
            return builder.or(lhsPredicate, rhsPredicate);
        };
    }

    private boolean isLeaf(String operator) {
        return !operator.equalsIgnoreCase("&&") &&
                !operator.equalsIgnoreCase("||");
    }

    private boolean isTernaryOperator(String operator) {
        return operator.equalsIgnoreCase("between");
    }

    private Predicate isEqualPredicate(Root<T> root,
                                       Join<T, ?> join,
                                       String operator,
                                       String fieldName,
                                       Object value,
                                       CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(join != null ? join.get(fieldName) : root.get(fieldName), value);
    }

    private Predicate isNotEqualPredicate(Root<T> root,
                                          Join<T, ?> join,
                                          String operator,
                                          String fieldName,
                                          Object value,
                                          CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(join != null ? join.get(fieldName) : root.get(fieldName), value);
    }

    private Predicate isLessOrEqualPredicate(Root<T> root,
                                             Join<T, ?> join,
                                             String operator,
                                             String fieldName,
                                             Object value,
                                             CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lessThanOrEqualTo(join != null ? join.get(fieldName) : root.get(fieldName), (Comparable) value);
    }

    private Predicate isLessPredicate(Root<T> root,
                                      Join<T, ?> join,
                                      String operator,
                                      String fieldName,
                                      Object value,
                                      CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lessThan(join != null ? join.get(fieldName) : root.get(fieldName), (Comparable) value);
    }

    private Predicate isGreaterPredicate(Root<T> root,
                                         Join<T, ?> join,
                                         String operator,
                                         String fieldName,
                                         Object value,
                                         CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThan(join != null ? join.get(fieldName) : root.get(fieldName), (Comparable) value);
    }

    private Predicate isGreaterOrEqualPredicate(Root<T> root,
                                                Join<T, ?> join,
                                                String operator,
                                                String fieldName,
                                                Object value,
                                                CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThanOrEqualTo(join != null ? join.get(fieldName) : root.get(fieldName), (Comparable) value);
    }

    private Predicate isLikePredicate(Root<T> root,
                                      Join<T, ?> join,
                                      String operator,
                                      String fieldName,
                                      Object value,
                                      CriteriaBuilder criteriaBuilder) {
        var likeExpression = String.format("%s", value);
        return criteriaBuilder.like(join != null ? join.get(fieldName) : root.get(fieldName), likeExpression);
    }

    private HashMap<String, BuilderPredicate<T>> binaryOperatorsMap = new HashMap<>();


    private Predicate buildPredicateForBinaryOperator(
            Root<T> root,
            JsonNode node,
            CriteriaBuilder criteriaBuilder
    ) {
        JsonNode lhs = node.get(MainTreeKeys.lhs);
        String operator = node.get(MainTreeKeys.operator).asText();
        String fieldName = lhs.asText();

        Join<T, ?> join = null;
        if (fieldName.contains(".")) { // Check for related entity
            // Split the attribute path
            String[] attributePath = fieldName.split("\\.");

            // Navigate through the join for each attribute except the last one
            join = root.join(attributePath[0]);
            if (attributePath.length > 1) {
                for (int i = 1; i < attributePath.length - 1; i++) {
                    join = join.join(attributePath[i]);
                }
            }

            fieldName = attributePath[attributePath.length - 1];
        }

        JsonNode rhs = node.get(MainTreeKeys.rhs);
        JsonNode hint = node.get(MainTreeKeys.type);

        if (!binaryOperatorsMap.containsKey(operator)) {
            throw new IllegalArgumentException("Unsupported binary operator");
        }

        Object value = null;
        if (hint != null && hint.asText().equals("Date")) {
            value = LocalDate.parse(rhs.asText());
        }
        if (value == null && rhs.isBoolean()) {
            value = rhs.asBoolean();
        }
        if (value == null && rhs.isInt()) {
            value = rhs.asInt();
        }
        if (value == null && rhs.isTextual()) {
            value = rhs.asText();
        }
        if (value == null) {
            throw new IllegalArgumentException("Unsupported rhs data type");
        }
        return binaryOperatorsMap.get(operator).execute(root, join, operator, fieldName, value, criteriaBuilder);

    }

    private Predicate buildPredicateForTernaryOperator(
            Root<T> root,
            JsonNode node,
            CriteriaBuilder criteriaBuilder
    ) {
        String operator = node.get(MainTreeKeys.operator).asText();
        JsonNode lhs = node.get(MainTreeKeys.lhs);
        String fieldName = lhs.asText();
        JsonNode hint = node.get(MainTreeKeys.type);
        JsonNode rangeStart = node.get(MainTreeKeys.rangeStart);
        JsonNode rangeEnd = node.get(MainTreeKeys.rangeEnd);

        Join<T, ?> join = null;
        if (fieldName.contains(".")) {
            String[] attributePath = fieldName.split("\\.");

            join = root.join(attributePath[0]);
            if (attributePath.length > 1) {
                for (int i = 1; i < attributePath.length - 1; i++) {
                    join = join.join(attributePath[i]);
                }
            }
        }

        switch (operator) {
            case "between":
                if (hint != null && hint.asText().equals("Date")) {
                    LocalDate ranteStartAsDate = LocalDate.parse(rangeStart.asText());
                    LocalDate ranteEndAsDate = LocalDate.parse(rangeEnd.asText());
                    return criteriaBuilder.between(join != null ? join.get(fieldName) : root.get(fieldName), ranteStartAsDate, ranteEndAsDate);
                }

                if (rangeStart.isInt()) {
                    return criteriaBuilder.between(join != null ? join.get(fieldName) : root.get(fieldName), rangeStart.asInt(), rangeEnd.asInt());
                }
                if (rangeStart.isFloat()) {
                    return criteriaBuilder.between(join != null ? join.get(fieldName) : root.get(fieldName), rangeStart.asDouble(), rangeEnd.asDouble());
                }
                throw new IllegalArgumentException("Invalid rangeStart or rangeEnd data type");

            default:
                throw new IllegalArgumentException("Unsupported ternary operator");
        }
    }
}