/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.program.r;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.Program;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRProgram;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.api.r.RProcess;
import de.clusteval.api.r.RProgramThread;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.cluster.Clustering;
import de.clusteval.utils.StringExt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.rosuda.REngine.REngineException;

/**
 * A type of program that encapsulates a program embedded in R.
 *
 * <p>
 * Note that the name of the method in R is not itself written in plaintext in
 * the
 * {@link #exec(de.clusteval.data.DataConfig, de.clusteval.program.ProgramConfig, String[], java.util.Map, java.util.Map)}
 * method of RPrograms, but instead it is defined in the corresponding program
 * configuration in the invocation format parameter.
 * <p>
 *
 * {@code
 * R programs can be added to ClustEval by
 *
 * 1. extending this class with your own class MyRProgram. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 *
 *   * :java:ref:`RProgram(Repository)` : The constructor of your class taking a repository parameter. This constructor has to be implemented and public, otherwise the framework will not be able to load your class.
 *   * :java:ref:`RProgram(MyRProgram)` : The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *   * :java:ref:`getAlias()` : See :java:ref:`Program.getAlias()`.
 *   * :java:ref:`getInvocationFormat()` : This is the invocation of the R method including potential parameters, that have to be defined in the program configuration.
 *   * :java:ref:`exec(DataConfig,ProgramConfig,String[],Map,Map)` : See :java:ref:`Program.exec(DataConfig,ProgramConfig,String[],Map,Map)` In this method the actual execution of the R Program happens. In here you have to implement the invocation of the R method via Rserve and any pre- and postcalculations necessary. The communications with R can be visualized by the following code snippet::
 *
 *       try {
 *         // precalculations
 *         double[] input = ...;
 *         ...
 *         MyRengine rEngine = new MyRengine("");
 *         try {
 *           rEngine.assign("input",input);
 *           rEngine.eval("result <- yourMethodInvocation()");
 *           REXP result = rEngine.eval("result@.Data");
 *           // postcalculations
 *           ...
 *         } catch (RserveException e) {
 *           e.printStackTrace();
 *         } finally {
 *           rEngine.close();
 *         }
 *       } catch (Exception e) {
 *         e.printStackTrace();
 *       }
 *       // for return type compatibility reasons
 *       return null;
 *
 * 2. Creating a jar file named MyRProgram.jar containing the MyRProgram.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/program/r/MyRProgram.class
 *
 * 3. Putting the MyRProgram.jar into the programs folder of the repository:
 *
 *   * <REPOSITORY ROOT>/programs * The backend server will recognize and try to
 * load the new program automatically the next time, the `RProgramFinderThread`
 * checks the filesystem.
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public abstract class RProgram extends Program implements RLibraryInferior, IRProgram {

    /**
     * Attribute used to store an rengine instance during execution of this
     * program.
     */
    protected IRengine rEngine;

    /**
     * Attribute used to store the dataset content during execution of this
     * program.
     */
    protected Object dataSetContent;

    /**
     * Attribute to store the data object ids during execution of this program.
     */
    protected String[] ids;

    /**
     * Attribute to store the pairwise similarites or absolute coordinates of
     * data objects during execution of this program.
     */
    protected double[][] x;

    /**
     * This method parses the major name given as a string, looks up the
     * corresponding RProgram in the repository and returns a new instance.
     *
     * @param repository The repository to lookup the RProgram.
     * @param rProgram   The major name (see {@link #getMajorName()}) of the
     *                   RProgram to return.
     * @return An instance of an RProgram corresponding to the passed string.
     * @throws UnknownRProgramException
     */
    public static RProgram parseFromString(final IRepository repository,
            String rProgram) throws UnknownRProgramException {
        Class<? extends RProgram> c = repository.getRegisteredClass(
                RProgram.class, "de.clusteval.program.r." + rProgram);

        try {
            Constructor<? extends RProgram> constr = c.getConstructor(IRepository.class);
            RProgram program = constr.newInstance(repository);
            return program;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 SecurityException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        throw new UnknownRProgramException("\"" + rProgram
                + "\" is not a known RProgram.");
    }

    /**
     * @param repository the repository this program should be registered at.
     * @param changeDate The change date of this program is used for equality
     *                   checks.
     * @param absPath    The absolute path of this program.
     * @throws RegisterException
     */
    public RProgram(IRepository repository, long changeDate, File absPath)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
    }

    /**
     * The copy constructor for rprograms.
     *
     * @param rProgram The object to clone.
     * @throws RegisterException
     */
    public RProgram(final RProgram rProgram) throws RegisterException {
        super(rProgram);
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#clone()
     */
    @Override
    public final RProgram clone() {
        try {
            return this.getClass().getConstructor(this.getClass()).newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /**
     * The major name of a RProgram corresponds to the simple name of its class.
     *
     * @return
     */
    @Override
    public String getMajorName() {
        return this.getClass().getSimpleName();
    }


    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.Program#exec(de.clusteval.data.DataConfig,
     * de.clusteval.program.ProgramConfig, java.lang.String[], java.util.Map,
     * java.util.Map)
     */
    @Override
    public final Process exec(final IDataConfig dataConfig,
            final IProgramConfig programConfig, final String[] invocationLine,
            final Map<String, String> effectiveParams,
            final Map<String, String> internalParams) throws RException {

        // 06.07.2014: execute r command in a thread.
        // then this thread can check for interrupt signal and forward it to
        // the
        // rengine.
        RProgramThread t;
        t = new RProgramThread(Thread.currentThread(), this,
                dataConfig, programConfig, invocationLine, effectiveParams, internalParams);
        t.start();
        return new RProcess(t);

    }

    /**
     * @return The r engine corresponding to this rprogram.
     */
    public IRengine getRengine() {
        return this.rEngine;
    }

    @Override
    public void beforeExec(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws RException,
                                                       RLibraryNotLoadedException, RNotAvailableException,
                                                       InterruptedException {

        // load the required R libraries
        String[] requiredLibraries;
        if (this.getClass().isAnnotationPresent(RLibraryRequirement.class)) {
            requiredLibraries = this.getClass()
                    .getAnnotation(RLibraryRequirement.class)
                    .requiredRLibraries();
        } else {
            requiredLibraries = new String[0];
        }
        for (String library : requiredLibraries) {
            rEngine.loadLibrary(library, this.getClass().getSimpleName());
        }

        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        // this will init the ids attribute
        this.dataSetContent = extractDataSetContent(dataConfig);

        rEngine.assign("ids", ids);
    }

    public void doExec(IDataConfig dataConfig, IProgramConfig programConfig,
            final String[] invocationLine, Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws InterruptedException, RException {
        rEngine.eval("result <- " + StringExt.paste(" ", invocationLine));

        // try {
        // t.join();
        // // rethrow exception from the r process, if any
        // if (t.getException() != null)
        // throw t.getException();
        // } catch (InterruptedException e) {
        // // forward the interruption to the r process
        // rEngine.interrupt();
        // repository.clearRengineForCurrentThread();
        // throw e;
        // }
    }

    @Override
    public void afterExec(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws RException, IOException, InterruptedException, ROperationNotSupported {
        // try {
        try {
            final String resultAsString = execResultToString(dataConfig,
                    programConfig, invocationLine, effectiveParams,
                    internalParams);

            File output = new File(internalParams.get("o"));

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
                bw.append(resultAsString);
            }
        } catch (StringIndexOutOfBoundsException e) {
            REngineException e2 = new REngineException(null,
                    "The R program returned an empty clustering");
            e2.initCause(e);
            throw new RException(e2.getMessage(), e2);
        }
        // } finally {
        // rEngine.close();
        // }
        this.x = null;
        this.dataSetContent = null;
        this.ids = null;
    }

    public String execResultToString(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws RException,
                                                       ROperationNotSupported, InterruptedException {
        Clustering resultClustering = Clustering.parseFromFuzzyCoeffMatrix(
                dataConfig.getRepository(), new File(internalParams.get("o")),
                ids, getFuzzyCoeffMatrixFromExecResult());

        StringBuilder sb = new StringBuilder();
        // TODO: changed 31.01.2014
        for (IProgramParameter<?> p : programConfig.getParams()) {
            sb.append(p.getName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\tClustering\n");

        for (IProgramParameter<?> p : programConfig.getParams()) {
            sb.append(effectiveParams.get(p.getName()));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\t");
        sb.append(resultClustering.toFormattedString());
        return sb.toString();
    }

    @Override
    public IProgram duplicate() {
        try {
            return this.getClass().getConstructor(this.getClass()).newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    @Override
    public void setEngine(IRengine engine) {
        this.rEngine = engine;
    }

    @Override
    public IRengine getEngine() {
        return rEngine;
    }
}