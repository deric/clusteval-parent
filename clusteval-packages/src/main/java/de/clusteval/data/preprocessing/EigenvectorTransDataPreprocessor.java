/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.data.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataMatrix;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 * 
 */
public class EigenvectorTransDataPreprocessor extends DataPreprocessor {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public EigenvectorTransDataPreprocessor(Repository repository,
			boolean register, long changeDate, File absPath)
			throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * @param other
	 * @throws RegisterException
	 */
	public EigenvectorTransDataPreprocessor(DataPreprocessor other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.data.preprocessing.DataPreprocessor#preprocess(de.clusteval
	 * .data.DataConfig)
	 */
	@Override
	public DataSet preprocess(DataSet data) throws InterruptedException {
		if (data instanceof RelativeDataSet)
			throw new IllegalArgumentException(
					"The eigenvector transformation preprocessor is only applicable to absolute coordinates");
		final AbsoluteDataSet dataSet = (AbsoluteDataSet) data;
		try {
			dataSet.loadIntoMemory();
			DataMatrix matrix = dataSet.getDataSetContent();
			try {
				MyRengine rEngine = repository.getRengineForCurrentThread();
				try {
					rEngine.assign("matrix", matrix.getData());
				} catch (REngineException e) {
					e.printStackTrace();
					// } catch (REXPMismatchException e) {
					// e.printStackTrace();
				} finally {
					rEngine.clear();
				}
			} catch (RserveException e) {
				e.printStackTrace();
			}
		} catch (InvalidDataSetFormatVersionException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (UnknownDataSetFormatException e) {
			e.printStackTrace();
		} finally {
			dataSet.unloadFromMemory();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.data.preprocessing.DataPreprocessor#getCompatibleDataSetFormats
	 * ()
	 */
	@Override
	public Set<String> getCompatibleDataSetFormats() {
		return new HashSet<String>(
				Arrays.asList(new String[]{"MatrixDataSetFormat"}));
	}
}
