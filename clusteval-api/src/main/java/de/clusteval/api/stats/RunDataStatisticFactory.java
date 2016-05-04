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
public class RunDataStatisticFactory extends ServiceFactory<IRunDataStatistic> {

    private static RunDataStatisticFactory instance;

    public static RunDataStatisticFactory getInstance() {
        if (instance == null) {
            instance = new RunDataStatisticFactory();
        }
        return instance;
    }

    private RunDataStatisticFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRunDataStatistic> list = Lookup.getDefault().lookupAll(IRunDataStatistic.class);
        for (IRunDataStatistic c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static IRunDataStatistic parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IRunDataStatistic parseFromString(IRepository repo, String name) throws UnknownProviderException {
        IRunDataStatistic inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    public static Set<IRunDataStatistic> parseFromString(IRepository repo, String[] names) throws UnknownProviderException {
        Set<IRunDataStatistic> res = new HashSet<>();
        for (String name : names) {
            res.add(parseFromString(repo, name));
        }
        return res;
    }
}
