/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api.run;

import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.repository.IRepository;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class RunRunnableFactory extends ServiceFactory<IRunRunnable> {

    private static RunRunnableFactory instance;

    public static RunRunnableFactory getInstance() {
        if (instance == null) {
            instance = new RunRunnableFactory();
        }
        return instance;
    }

    private RunRunnableFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRunRunnable> list = Lookup.getDefault().lookupAll(IRunRunnable.class);
        for (IRunRunnable c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static IRunRunnable parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IRunRunnable parseFromString(IRepository repo, String name) throws UnknownProviderException {
        IRunRunnable inst = getInstance().getProvider(name);
        return inst;
    }
}
