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
package de.clusteval.api.data;

import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class DataPreprocessorFactory extends ServiceFactory<DataPreprocessor> {

    private static DataPreprocessorFactory instance;

    public static DataPreprocessorFactory getInstance() {
        if (instance == null) {
            instance = new DataPreprocessorFactory();
        }
        return instance;
    }

    private DataPreprocessorFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends DataPreprocessor> list = Lookup.getDefault().lookupAll(DataPreprocessor.class);
        for (DataPreprocessor c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static DataPreprocessor parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static DataPreprocessor parseFromString(IRepository repo, String name) throws UnknownProviderException {
        DataPreprocessor inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    public static List<DataPreprocessor> parseFromString(IRepository repo, String[] names) throws UnknownProviderException {
        List<DataPreprocessor> res = new LinkedList<>();
        for (String name : names) {
            res.add(parseFromString(repo, name));
        }
        return res;
    }
}
