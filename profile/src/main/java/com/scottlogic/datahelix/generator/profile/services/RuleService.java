/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.profile.services;

import com.google.inject.Inject;
import com.scottlogic.datahelix.generator.common.profile.Fields;
import com.scottlogic.datahelix.generator.core.profile.Rule;
import com.scottlogic.datahelix.generator.core.profile.constraints.Constraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.IsNullConstraint;
import com.scottlogic.datahelix.generator.profile.custom.CustomConstraintFactory;
import com.scottlogic.datahelix.generator.profile.dtos.RuleDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuleService
{
    private final ConstraintService constraintService;
    private final CustomConstraintFactory customConstraintFactory;

    @Inject
    public RuleService(
        ConstraintService constraintService,
        CustomConstraintFactory customConstraintFactory)
    {
        this.constraintService = constraintService;
        this.customConstraintFactory = customConstraintFactory;
    }

    public List<Rule> createRules(List<RuleDTO> ruleDTOs, Fields fields)
    {
        List<Rule> rules =  ruleDTOs.stream()
            .map(dto -> new Rule(dto.description, constraintService.createConstraints(dto.constraints, fields)))
            .collect(Collectors.toList());

        createNotNullableRule(fields).ifPresent(rules::add);
        createSpecificTypeRule(fields).ifPresent(rules::add);
        createCustomGeneratorRule(fields, customConstraintFactory).ifPresent(rules::add);

        return rules;
    }

    private Optional<Rule> createNotNullableRule(Fields fields)
    {
        List<Constraint> notNullableConstraints =  fields.stream()
            .filter(field -> !field.isNullable())
            .map(field -> new IsNullConstraint(field).negate())
            .collect(Collectors.toList());

        return notNullableConstraints.isEmpty()
            ? Optional.empty()
            : Optional.of(new Rule("not-nullable", notNullableConstraints));
    }

    private Optional<Rule> createSpecificTypeRule(Fields fields)
    {
        List<Constraint> specificTypeConstraints = fields.stream()
            .map(constraintService::createSpecificTypeConstraint)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        return specificTypeConstraints.isEmpty()
            ? Optional.empty()
            : Optional.of(new Rule("specific-types", specificTypeConstraints));
    }

    private Optional<Rule> createCustomGeneratorRule(
        Fields fields,
        CustomConstraintFactory customConstraintFactory)
    {
        List<Constraint> customGeneratorConstraints = fields.stream()
            .filter(f -> f.usesCustomGenerator())
            .map(f -> customConstraintFactory.create(f, f.getCustomGeneratorName()))
            .collect(Collectors.toList());

        return customGeneratorConstraints.isEmpty()
            ? Optional.empty()
            : Optional.of(new Rule("generators", customGeneratorConstraints));
    }
}