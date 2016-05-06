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
package de.clusteval.cluster;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.Pair;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.ClusteringEvaluationFactory;
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.utils.TextFileMapParser;
import de.clusteval.utils.TextFileParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 * A parser for files containing parameter sets and clusterings.
 *
 * @author Christian Wiwie
 */
public class ClusteringParser extends TextFileParser {

    protected IRepository repository;

    protected boolean parseQualities;

    /**
     * This variable holds the results after parsing
     */
    protected Pair<ParameterSet, Clustering> result;

    /**
     * A temporary variable of no use after parsing.
     */
    protected List<String> params;

    /**
     * Instantiates a new clustering parser.
     *
     * @param repository
     *
     * @param absFilePath the abs file path
     * @param parseQualities True, if the qualities of the clusterings should
     * also be parsed. Those will be taken from .qual-files.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ClusteringParser(final IRepository repository,
            final String absFilePath, final boolean parseQualities)
            throws IOException {
        super(absFilePath, new int[]{0}, new int[]{1});
        this.repository = repository;
        this.setLockTargetFile(true);
        this.params = new ArrayList<>();
        this.parseQualities = parseQualities;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.parse.TextFileParser#processLine(java.lang.String[],
	 * java.lang.String[])
     */
    @Override
    protected void processLine(String[] key, String[] value) {

        String parameterString = key[0];

        if (this.currentLine == 0) {
            String[] params = parameterString.split(",");
            for (String param : params) {
                this.params.add(param.intern());
            }
            return;
        }

        Clustering result;
        try {
            RepositoryController ctrl = RepositoryController.getInstance();
            IRepository repo = ctrl.getRepositoryForPath(absoluteFilePath);
            if (repo.getParent() != null) {
                repo = repo.getParent();
            }

            result = new Clustering(repo,
                    new File(absoluteFilePath).lastModified(), new File(
                            absoluteFilePath));
            String[] params = parameterString.split(",");

            ParameterSet paramValues = new ParameterSet();
            for (int pos = 0; pos < this.params.size(); pos++) {
                paramValues.put(this.params.get(pos), params[pos]);
            }

            this.result = Pair.getPair(paramValues, result);
        } catch (NoRepositoryFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.parse.TextFileParser#finishProcess()
     */
    @Override
    public void finishProcess() {
        // parse qualities
        if (parseQualities) {
            final File qualityFile = new File(this.absoluteFilePath.replace(
                    ".conv", ".qual"));
            if (qualityFile.exists()) {
                try {
                    TextFileMapParser parser = new TextFileMapParser(
                            qualityFile.getAbsolutePath(), 0, 1);
                    parser.process();
                    Map<String, String> result = parser.getResult();
                    ClusteringQualitySet qualitySet = new ClusteringQualitySet();
                    for (String measure : result.keySet()) {
                        ClusteringEvaluation clMeasure;

                        // TODO: ClusteringQualityMeasureParameters not
                        // initialized
                        clMeasure = ClusteringEvaluationFactory.parseFromString(this.repository, measure,
                                new ClusteringEvaluationParameters());
                        qualitySet.put(clMeasure, ClustEvalValue
                                .getForDouble(Double.parseDouble(result
                                        .get(measure))));
                    }
                    this.result.getSecond().setQualities(qualitySet);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnknownProviderException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        super.finishProcess();
    }

    /**
     * @return A map containing parameter sets and corresponding clusterings.
     */
    public Pair<ParameterSet, Clustering> getClusterings() {
        return this.result;
    }
}
