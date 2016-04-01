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
package de.clusteval.api.factory;

import de.clusteval.api.stats.IRunStatistic;
import java.util.Collection;
import java.util.LinkedHashMap;
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
}
