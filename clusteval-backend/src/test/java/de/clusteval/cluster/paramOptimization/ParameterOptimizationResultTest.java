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
package de.clusteval.cluster.paramOptimization;

import de.clusteval.api.opt.IParamOptResult;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.run.RunResultFactory;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.plot.Plotter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class ParameterOptimizationResultTest extends AbstractClustEvalTest {

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        List<IParamOptResult> result = new ArrayList<>();
        final IRun run = RunResultFactory
                .parseParamOptResult(
                        getRepository(),
                        new File("testCaseRepository/results/11_20_2012-12_45_04_all_vs_DS1")
                        .getAbsoluteFile(), result, false, false, false);
        for (IRunResult res : result) {
            IParamOptResult r = (IParamOptResult) res;
            Plotter.plotParameterOptimizationResult(r);
        }
        System.out.println(result);
    }
}
