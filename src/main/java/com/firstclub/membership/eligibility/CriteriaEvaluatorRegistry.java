package com.firstclub.membership.eligibility;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CriteriaEvaluatorRegistry {

    private final Map<String, CriteriaEvaluator> evaluators;

    public CriteriaEvaluatorRegistry(List<CriteriaEvaluator> evaluators) {
        this.evaluators = evaluators.stream()
                .collect(Collectors.toMap(CriteriaEvaluator::getSupportedType, Function.identity()));
    }

    public CriteriaEvaluator get(String criteriaType) {
        CriteriaEvaluator evaluator = evaluators.get(criteriaType);
        if (evaluator == null) {
            throw new IllegalArgumentException(
                    "No evaluator registered for criteria type: " + criteriaType);
        }
        return evaluator;
    }
}
