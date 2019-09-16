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

package com.scottlogic.deg.profile.reader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.scottlogic.deg.common.profile.*;
import com.scottlogic.deg.profile.dto.ConstraintDTO;
import com.scottlogic.deg.profile.serialisation.ProfileDeserialiser;
import com.scottlogic.deg.profile.dto.ProfileDTO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JsonProfileReader is responsible for reading and validating a profile from a path to a profile JSON file.
 * It returns a Profile object for consumption by a generator
 */
public class JsonProfileReader implements ProfileReader {
    private final File profileFile;
    private final MainConstraintReader mainConstraintReader;

    @Inject
    public JsonProfileReader(@Named("config:profileFile") File profileFile, MainConstraintReader mainConstraintReader) {
        this.profileFile = profileFile;
        this.mainConstraintReader = mainConstraintReader;
    }

    public Profile read() throws IOException {
        byte[] encoded = Files.readAllBytes(profileFile.toPath());
        String profileJson = new String(encoded, StandardCharsets.UTF_8);

        return this.read(profileJson);
    }

    public Profile read(String profileJson) throws IOException {
        ProfileDTO profileDto = new ProfileDeserialiser()
            .deserialise(profileJson);

        if (profileDto.fields == null) {
            throw new InvalidProfileException("Profile is invalid: 'fields' have not been defined.");
        }
        if (profileDto.rules == null) {
            throw new InvalidProfileException("Profile is invalid: 'rules' have not been defined.");
        }

        Collection<String> uniqueList = getUniqueFields(profileDto);
        Map<String, String> fieldFormating = getfieldFomatting(profileDto);

        ProfileFields profileFields = new ProfileFields(
            profileDto.fields.stream()
                .map(fDto -> new Field(fDto.name, uniqueList.contains(fDto.name), fieldFormating.get(fDto.name)))
                .collect(Collectors.toList()));

        Collection<Rule> rules = profileDto.rules.stream().map(
            r -> {
                if (r.constraints.isEmpty()) {
                    throw new InvalidProfileException("Profile is invalid: unable to find 'constraints' for rule: " + r.rule);
                }
                RuleInformation constraintRule = new RuleInformation(r.rule);
                return new Rule(constraintRule, mainConstraintReader.getSubConstraints(profileFields, r.constraints));
            }).collect(Collectors.toList());

        return new Profile(profileFields, rules, profileDto.description);
    }

    private Collection<String> getUniqueFields(ProfileDTO profileDto) {
        return getTopLevelConstraintsOfType(profileDto, "unique")
            .map(constraintDTO -> constraintDTO.field)
            .collect(Collectors.toSet());
    }

    private Map<String, String> getfieldFomatting(ProfileDTO profileDto) {
        return getTopLevelConstraintsOfType(profileDto, "formattedAs")
            .collect(Collectors.toMap(
                constraintDTO -> constraintDTO.field,
                constraintDTO -> (String)constraintDTO.value
            ));
    }

    private Stream<ConstraintDTO> getTopLevelConstraintsOfType(ProfileDTO profileDto, String constraint) {
        return profileDto.rules.stream()
            .flatMap(ruleDTO -> ruleDTO.constraints.stream())
            .flatMap(this::getConstraintOrAllOfConstraints)
            .filter(constraintDTO -> constraintDTO.is != null)
            .filter(constraintDTO -> constraintDTO.is.equals(constraint));
    }

    private Stream<ConstraintDTO> getConstraintOrAllOfConstraints(ConstraintDTO constraintDTO) {
        if (constraintDTO.allOf != null){
            return constraintDTO.allOf.stream();
        }

        return Stream.of(constraintDTO);
    }
}