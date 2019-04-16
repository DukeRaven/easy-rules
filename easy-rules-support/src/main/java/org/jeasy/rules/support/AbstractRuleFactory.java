package org.jeasy.rules.support;

import org.jeasy.rules.api.Rule;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractRuleFactory<CONTEXT> {

    private static final List<String> ALLOWED_COMPOSITE_RULE_TYPES = Arrays.asList(
            UnitRuleGroup.class.getSimpleName(),
            ConditionalRuleGroup.class.getSimpleName(),
            ActivationRuleGroup.class.getSimpleName()
    );

    protected Rule createRule(RuleDefinition ruleDefinition, CONTEXT context) {
        if (ruleDefinition.isCompositeRule()) {
            return createCompositeRule(ruleDefinition, context);
        } else {
            return createSimpleRule(ruleDefinition, context);
        }
    }

    protected abstract Rule createSimpleRule(RuleDefinition ruleDefinition, CONTEXT parserContext);

    protected Rule createCompositeRule(RuleDefinition ruleDefinition, CONTEXT parserContext) {
        CompositeRule compositeRule;
        String name = ruleDefinition.getName();
        switch (ruleDefinition.getCompositeRuleType()) {
            case "UnitRuleGroup":
                compositeRule = new UnitRuleGroup(name);
                break;
            case "ActivationRuleGroup":
                compositeRule = new ActivationRuleGroup(name);
                break;
            case "ConditionalRuleGroup":
                compositeRule = new ConditionalRuleGroup(name);
                break;
            default:
                throw new IllegalArgumentException("Invalid composite rule type, must be one of " + ALLOWED_COMPOSITE_RULE_TYPES);
        }
        compositeRule.setDescription(ruleDefinition.getDescription());
        compositeRule.setPriority(ruleDefinition.getPriority());

        for (RuleDefinition composingRuleDefinition : ruleDefinition.getComposingRules()) {
            compositeRule.addRule(createRule(composingRuleDefinition, parserContext));
        }

        return compositeRule;
    }

}
