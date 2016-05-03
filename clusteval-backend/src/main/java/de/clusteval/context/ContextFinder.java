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
package de.clusteval.context;

import de.clusteval.api.AbsContext;
import de.clusteval.api.IContext;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.JARFinder;
import de.clusteval.utils.RecursiveSubDirectoryIterator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;

/**
 * @author Christian Wiwie
 */
public class ContextFinder extends JARFinder<IContext> {

    /**
     * Instantiates a new data set format finder.
     *
     * @param repository
     *                   the repository
     * @throws RegisterException
     */
    public ContextFinder(final IRepository repository) throws RegisterException {
        super(repository, IContext.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
     */
    @Override
    public boolean checkFile(File file) {
        return file.getName().endsWith("Context.jar");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.JARFinder#classNameForJARFile(java.io.File)
     */
    @Override
    public String[] classNamesForJARFile(File f) {
        return new String[]{"de.clusteval.context."
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
                && this.repository.isClassRegistered(AbsContext.class,
                        "de.clusteval.context."
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
        URL url = f.toURI().toURL();
        return new ContextURLClassLoader(this, new URL[]{url}, parent);
    }
}

class ContextURLClassLoader extends URLClassLoader {

    protected ContextFinder parent;

    /**
     * @param urls
     * @param parent
     * @param loaderParent
     */
    public ContextURLClassLoader(ContextFinder parent, URL[] urls,
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

        if (name.startsWith("de.clusteval.context")
                && !name.equals("de.clusteval.context.Context")) {
            if (name.endsWith("Context")) {
                @SuppressWarnings("unchecked")
                Class<? extends AbsContext> context = (Class<? extends AbsContext>) result;

                this.parent.getRepository().registerClass(IContext.class,
                        context);
            }
        }
        return result;
    }
}
