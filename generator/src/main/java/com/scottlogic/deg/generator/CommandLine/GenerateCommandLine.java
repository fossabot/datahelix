package com.scottlogic.deg.generator.CommandLine;

import com.scottlogic.deg.generator.GenerateExecute;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;

public class GenerateCommandLine extends CommandLineBase implements GenerationConfigSource {

    @CommandLine.Parameters(index = "0", description = "The path of the profile json file.")
    private File profileFile;

    @CommandLine.Parameters(index = "1", description = "The path to write the generated data file to.")
    private Path outputPath;

    @CommandLine.Option(names = {"-t", "--t"},
        description = "Determines the type of data generation performed (FULL_SEQUENTIAL, INTERESTING, RANDOM).",
        defaultValue = "INTERESTING")
    private GenerationConfig.DataGenerationType generationType;

    @CommandLine.Option(names = {"-c", "--c"},
        description = "Determines the type of combination strategy used (pinning, exhaustive, minimal).",
        defaultValue = "PINNING")
    private GenerationConfig.CombinationStrategyType combinationType = GenerationConfig.CombinationStrategyType.PINNING;

    @CommandLine.Option(
        names = {"--no-optimise"},
        description = "Prevents tree optimisation",
        hidden = true)
    private boolean dontOptimise;

    @CommandLine.Option(
        names = {"--no-partition"},
        description = "Prevents tree partitioning",
        hidden = true)
    private boolean dontPartitionTrees;

    @CommandLine.Option(names = {"-w", "--w"},
        description = "Determines the tree walker that should be used.",
        defaultValue = GenerationConfig.Constants.WalkerTypes.DEFAULT,
        hidden = true)
    private GenerationConfig.TreeWalkerType walkerType;

    @CommandLine.Option(
        names = {"-n", "--n", "--max-rows"},
        description = "Defines the maximum number of rows that should be generated")
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private long maxRows = GenerationConfig.Constants.DEFAULT_MAX_ROWS;

    @CommandLine.Option(
        names = {"-v", "--v", "--validate-profile"},
        description = "Defines whether to validate the profile (" +
            true+ ", " +
            false + ").")
    private boolean validateProfile;

    @CommandLine.Option(
        names = {"--trace-constraints"},
        description = "Defines whether constraint tracing is enabled for the output")
    private boolean enableTracing;

    @Override
    public boolean shouldDoPartitioning() {
        return !this.dontPartitionTrees;
    }

    @Override
    public boolean dontOptimise() {
        return this.dontOptimise;
    }

    @Override
    public File getProfileFile() {
        return this.profileFile;
    }

    @Override
    public Path getOutputPath() {
        return this.outputPath;
    }

    @Override
    public boolean isEnableTracing() {
        return this.enableTracing;
    }

    @Override
    public GenerationConfig.DataGenerationType getGenerationType() {
        return this.generationType;
    }

    @Override
    public GenerationConfig.CombinationStrategyType getCombinationStrategyType() {
        return this.combinationType;
    }

    @Override
    public GenerationConfig.TreeWalkerType getWalkerType() {
        return this.walkerType;
    }

    @Override
    public long getMaxRows() {
        return this.maxRows;
    }

    @Override
    public boolean getValidateProfile() {
        return this.validateProfile;
    }

    @Override
    protected Class<? extends Runnable> getExecutorType() {
        return GenerateExecute.class;
    }
}