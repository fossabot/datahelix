package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.restrictions.GranularityRestrictionsMerger;

import java.util.Optional;

public class GranularityRestrictionsMergeOperation implements RestrictionMergeOperation{
    private static final GranularityRestrictionsMerger granularityRestrictionsMerger = new GranularityRestrictionsMerger();

    @Override
    public Optional<FieldSpec> applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merged) {
        return Optional.of(merged.withGranularityRestrictions(
            granularityRestrictionsMerger.merge(
                left.getGranularityRestrictions(),
                right.getGranularityRestrictions()),
            FieldSpecSource.fromFieldSpecs(left, right)));
    }
}