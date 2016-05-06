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
package de.clusteval.run;

import de.clusteval.api.run.Run;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class RunTest extends AbstractClustEvalTest {

    @Test
    public void testRun() throws RegisterException {
        /*
         * Ensure that a run is registered in the constructor
         */
        Run run = new ClusteringRun(this.getRepository(), context,
                System.currentTimeMillis(), new File("test"),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>());
        Assert.assertTrue(run == this.getRepository().getRegisteredObject(run));
    }
}
