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
package de.clusteval.api.stats;

import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class RunStatisticFactory extends ServiceFactory<IRunStatistic> {

    private static RunStatisticFactory instance;

    public static RunStatisticFactory getInstance() {
        if (instance == null) {
            instance = new RunStatisticFactory();
        }
        return instance;
    }

    private RunStatisticFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRunStatistic> list = Lookup.getDefault().lookupAll(IRunStatistic.class);
        for (IRunStatistic c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static IRunStatistic parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IRunStatistic parseFromString(IRepository repo, String name) throws UnknownProviderException {
        IRunStatistic inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    public static Set<IRunStatistic> parseFromString(IRepository repo, String[] names) throws UnknownProviderException {
        Set<IRunStatistic> res = new HashSet<>();
        for (String name : names) {
            res.add(parseFromString(repo, name));
        }
        return res;
    }
}
