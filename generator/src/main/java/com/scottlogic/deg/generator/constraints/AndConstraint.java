package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.inputs.validation.VisitableProfileElement;
import com.scottlogic.deg.generator.inputs.validation.ProfileVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class AndConstraint implements IConstraint, VisitableProfileElement
{
    public final Collection<IConstraint> subConstraints;

    public AndConstraint(Collection<IConstraint> subConstraints) {
        this.subConstraints = subConstraints;
    }

    public AndConstraint(IConstraint... subConstraints) {
        this(Arrays.asList(subConstraints));
    }

    @Override
    public Collection<Field> getFields() {
        return subConstraints.stream()
            .flatMap(constraint -> constraint.getFields().stream())
            .collect(Collectors.toList());
    }

    public void accept(ProfileVisitor visitor){
        subConstraints.forEach(visitor::visit);
        visitor.visit(this);
    }

    @Override
    public String toDotLabel(){
        throw new UnsupportedOperationException("AND constraints should be consumed during conversion to decision trees");

//        return String.format("And (%s)", subConstraints.stream()
//            .map(x -> x.toDotLabel()).collect(Collectors.joining(", ")));
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AndConstraint otherConstraint = (AndConstraint) o;
        return Objects.equals(subConstraints, otherConstraint.subConstraints);
    }

    @Override
    public int hashCode(){
        return Objects.hash("AND", subConstraints);
    }
}
