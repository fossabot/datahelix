package com.scottlogic.deg.profiler

import com.scottlogic.deg.analyser.field_analyser.GenericFieldAnalyser
import com.scottlogic.deg.analyser.field_analyser.numeric_analyser.NaiveNumericAnalyser
import com.scottlogic.deg.analyser.field_analyser.string_analyser.NaiveStringAnalyser
import com.scottlogic.deg.analyser.field_analyser.timestamp_analyser.NaiveTimestampAnalyser
import com.scottlogic.deg.schemas.v3.{RuleDTO, V3ProfileDTO}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._

case class Location(lat: Double, lon: Double)

class Profiler(df: DataFrame) {
    def profile(): V3ProfileDTO = {
        new V3ProfileDTO{
            def fields = df.schema.fields.map(field => field.name).toSeq;
            def rules = df.schema.fields.map(field => (field.dataType match {
                case DoubleType | LongType | IntegerType => new NaiveNumericAnalyser(df, field)
                case TimestampType => new NaiveTimestampAnalyser(df, field)
                case StringType => new NaiveStringAnalyser(df, field)
                case _ => new GenericFieldAnalyser(df, field)
            }).constructDTOField()).toSeq;
        }
    }
}