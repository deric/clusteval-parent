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

import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.program.ProgramConfig;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.FileNotFoundException;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class RProgramConfigTest extends AbstractClustEvalTest {

    @Test
    public void testKMeansCompatibleDataSetFormats()
            throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigurationException, InterruptedException {

        ProgramConfig programConfig = this.getRepository().findByName(ProgramConfig.class,
                "KMeans_Clustering");
        assertNotNull("failed to load KMeans_Clustering config", programConfig);
        Set<IDataSetFormat> dataSetFormats = programConfig.getCompatibleDataSetFormats();
        assertEquals(1, dataSetFormats.size());
        IDataSetFormat format = dataSetFormats.iterator().next();
        assertEquals("MatrixDataSetFormat", format.getClass().getSimpleName());
    }

    @Test
    public void testKMeansRunResultFormat()
            throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigurationException, InterruptedException {

        ProgramConfig programConfig = this.getRepository().findByName(ProgramConfig.class,
                "KMeans_Clustering");
        assertNotNull("failed to load KMeans_Clustering config", programConfig);
        IRunResultFormat format = programConfig.getOutputFormat();
        assertEquals("TabSeparatedRunResultFormat", format.getClass().getSimpleName());
    }
}
