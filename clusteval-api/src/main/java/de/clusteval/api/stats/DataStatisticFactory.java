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
import de.clusteval.api.stats.IDataStatistic;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class DataStatisticFactory extends ServiceFactory<IDataStatistic> {

    private static DataStatisticFactory instance;

    public static DataStatisticFactory getInstance() {
        if (instance == null) {
            instance = new DataStatisticFactory();
        }
        return instance;
    }

    private DataStatisticFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IDataStatistic> list = Lookup.getDefault().lookupAll(IDataStatistic.class);
        for (IDataStatistic c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
