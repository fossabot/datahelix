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

package com.scottlogic.deg.generator.fieldspecs.relations;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.restrictions.linear.DateTimeLimit;
import com.scottlogic.deg.generator.restrictions.linear.DateTimeRestrictions;
import com.scottlogic.deg.generator.restrictions.linear.Limit;

import java.time.OffsetDateTime;

public class BeforeDateRelation extends AbstractDateInequalityRelation {
    private final boolean inclusive;

    public BeforeDateRelation(Field main, Field other, boolean inclusive) {
        super(main, other);
        this.inclusive = inclusive;
    }

    @Override
    public Limit<OffsetDateTime> dateTimeLimitExtractingFunction(DateTimeRestrictions restrictions) {
        if (restrictions != null) {
            return restrictions.getMin();
        } else {
            return null;
        }
    }

    @Override
    protected DateTimeRestrictions appendValueToRestrictions(OffsetDateTime value) {
        return new DateTimeRestrictions(new DateTimeLimit(value, inclusive), DateTimeRestrictions.DATETIME_MAX_LIMIT);
    }

    @Override
    public FieldSpecRelations inverse() {
        return new AfterDateRelation(other(), main(), inclusive);
    }
}
