/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.data.dataset.generator;

import de.clusteval.api.exceptions.DataSetGenerationException;
import de.clusteval.api.exceptions.GoldStandardGenerationException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"mlbench"})
public class SpiralsDataSetGenerator extends DataSetGenerator {

    protected int numberOfPoints;

    protected int numberCycles;

    protected double standardDeviation;

    /**
     * Temp variable for the goldstandard classes.
     */
    private int[] classes;

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public SpiralsDataSetGenerator(IRepository repository, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public SpiralsDataSetGenerator(DataSetGenerator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.generator.DataSetGenerator#getOptions()
     */
    @Override
    public Options getOptions() {
        Options options = new Options();

        OptionBuilder.withArgName("n");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The number of points.");
        Option option = OptionBuilder.create("n");
        options.addOption(option);

        OptionBuilder.withArgName("c");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The number of cycles.");
        option = OptionBuilder.create("c");
        options.addOption(option);

        OptionBuilder.withArgName("sd");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The standard deviation.");
        option = OptionBuilder.create("sd");
        options.addOption(option);

        return options;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.generator.DataSetGenerator#generatesGoldStandard()
     */
    @Override
    public boolean generatesGoldStandard() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.generator.DataSetGenerator#handleOptions(org.apache.commons
     * .cli.CommandLine)
     */
    @Override
    public void handleOptions(CommandLine cmd) throws ParseException {
        if (cmd.getArgList().size() > 0) {
            throw new ParseException("Unknown parameters: "
                    + Arrays.toString(cmd.getArgs()));
        }

        if (cmd.hasOption("n")) {
            this.numberOfPoints = Integer.parseInt(cmd.getOptionValue("n"));
        } else {
            this.numberOfPoints = 100;
        }

        if (cmd.hasOption("c")) {
            this.numberCycles = Integer.parseInt(cmd.getOptionValue("c"));
        } else {
            this.numberCycles = 2;
        }

        if (cmd.hasOption("sd")) {
            this.standardDeviation = Double.parseDouble(cmd
                    .getOptionValue("sd"));
        } else {
            this.standardDeviation = 0;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.generator.DataSetGenerator#generateDataSet()
     */
    @Override
    public void generateDataSet() throws DataSetGenerationException, InterruptedException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            rEngine.eval("library(mlbench)");
            rEngine.eval("result <- mlbench.spirals(n=" + this.numberOfPoints
                    + ",cycles=" + this.numberCycles + ",sd="
                    + this.standardDeviation + ");");
            coords = rEngine.eval("result$x").asDoubleMatrix();
            classes = rEngine.eval("result$classes").asIntegers();

        } catch (RException | InterruptedException e) {
            LOG.error("failed to generate", e);
            throw new DataSetGenerationException("The dataset could not be generated!");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.generator.DataSetGenerator#generateGoldStandard()
     */
    @Override
    public GoldStandard generateGoldStandard() throws GoldStandardGenerationException {

        try {
            // goldstandard file
            File goldStandardFile = new File(FileUtils.buildPath(
                    this.repository.getBasePath(GoldStandard.class),
                    this.getFolderName(), this.getFileName()));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                    goldStandardFile))) {
                for (int row = 0; row < classes.length; row++) {
                    writer.append((row + 1) + "\t" + classes[row] + ":1.0");
                    writer.newLine();
                }
            }

            return new GoldStandard(repository,
                    goldStandardFile.lastModified(), goldStandardFile);

        } catch (IOException | RegisterException e) {
            LOG.error("failed to generate", e);
            e.printStackTrace();
        }
        throw new GoldStandardGenerationException(
                "The goldstandard could not be generated!");
    }
}
