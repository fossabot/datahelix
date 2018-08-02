package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.Field;

import java.util.ArrayList;
import java.util.Collection;

class DecisionTreeProfile implements IDecisionTreeProfile {
    private final Collection<Field> fields;
    private final Collection<? extends IRuleDecisionTree> rules;

    DecisionTreeProfile(Collection<Field> fields, Collection<? extends IRuleDecisionTree> rules) {
        this.fields = fields;
        this.rules = rules;
    }

    @Override
    public Collection<Field> getFields() {
        return fields;
    }

    @Override
    public Collection<IRuleDecisionTree> getDecisionTrees() {
        return new ArrayList<>(rules);
    }
}
