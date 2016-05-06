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
package de.clusteval.run.result.postprocessing;

import de.clusteval.api.run.result.RunResultPostprocessor;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.IRunResultPostprocessor;
import de.clusteval.utils.JARFinder;
import de.clusteval.utils.RecursiveSubDirectoryIterator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import org.openide.util.Utilities;

/**
 * @author Christian Wiwie
 */
public class RunResultPostprocessorFinder extends JARFinder<IRunResultPostprocessor> {

    /**
     * Instantiates a new data set generator finder.
     *
     * @param repository
     *                   the repository
     * @throws RegisterException
     */
    public RunResultPostprocessorFinder(final IRepository repository) throws RegisterException {
        super(repository, IRunResultPostprocessor.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
     */
    @Override
    public boolean checkFile(File file) {
        return file.getName().endsWith("RunResultPostprocessor.jar");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.JARFinder#classNameForJARFile(java.io.File)
     */
    @Override
    public String[] classNamesForJARFile(File f) {
        return new String[]{"de.clusteval.run.result.postprocessing."
            + f.getName().replace(".jar", "")};
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#getIterator()
     */
    @Override
    public Iterator<File> getIterator() {
        return new RecursiveSubDirectoryIterator(getBaseDir());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.JARFinder#isJARLoaded(java.io.File)
     */
    @Override
    protected boolean isJARLoaded(File f) {
        return super.isJARLoaded(f)
                && this.repository.isClassRegistered(
                        RunResultPostprocessor.class,
                        "de.clusteval.run.result.postprocessing."
                        + f.getName().replace(".jar", ""));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.JARFinder#getURLClassLoader(java.io.File)
     */
    @Override
    protected URLClassLoader getURLClassLoader0(File f, final ClassLoader parent)
            throws MalformedURLException {
        URL url = Utilities.toURI(f).toURL();
        return new RunResultPostprocessorURLClassLoader(this, new URL[]{url},
                parent);
    }
}

class RunResultPostprocessorURLClassLoader extends URLClassLoader {

    protected RunResultPostprocessorFinder parent;

    /**
     * @param urls
     * @param parent
     * @param loaderParent
     */
    public RunResultPostprocessorURLClassLoader(
            RunResultPostprocessorFinder parent, URL[] urls,
            ClassLoader loaderParent) {
        super(urls, loaderParent);
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> result = super.loadClass(name, true);

        if (name.startsWith("de.clusteval.run.result.postprocessing")
                && !name.equals("de.clusteval.run.result.postprocessing.RunResultPostprocessor")) {
            if (name.endsWith("RunResultPostprocessor")) {
                @SuppressWarnings("unchecked")
                Class<? extends RunResultPostprocessor> dataSetGenerator = (Class<? extends RunResultPostprocessor>) result;

                this.parent.getRepository().registerClass(
                        RunResultPostprocessor.class, dataSetGenerator);
            }
        }
        return result;
    }
}
