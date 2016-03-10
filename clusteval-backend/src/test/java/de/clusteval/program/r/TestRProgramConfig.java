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

import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.program.ProgramConfig;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.FileNotFoundException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class TestRProgramConfig extends AbstractClustEvalTest {

    @Test
    public void testKMeansCompatibleDataSetFormats()
            throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigNotFoundException, RepositoryConfigurationException, InterruptedException {

        ProgramConfig programConfig = this.getRepository().getStaticObjectWithName(ProgramConfig.class,
                "KMeans_Clustering");
        assertNotNull("failed to load KMeans_Clustering config", programConfig);
        List<DataSetFormat> dataSetFormats = programConfig.getCompatibleDataSetFormats();
        assertEquals(1, dataSetFormats.size());
        DataSetFormat format = dataSetFormats.get(0);
        assertEquals("MatrixDataSetFormat", format.getClass().getSimpleName());
    }

    @Test
    public void testKMeansRunResultFormat()
            throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigNotFoundException, RepositoryConfigurationException, InterruptedException {

        ProgramConfig programConfig = this.getRepository().getStaticObjectWithName(ProgramConfig.class,
                "KMeans_Clustering");
        assertNotNull("failed to load KMeans_Clustering config", programConfig);
        RunResultFormat format = programConfig.getOutputFormat();
        assertEquals("TabSeparatedRunResultFormat", format.getClass().getSimpleName());
    }
}
