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
package de.clusteval.api.repository;

import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class RepositoryFactory extends ServiceFactory<IRepository> {

    private static RepositoryFactory instance;

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    private RepositoryFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRepository> list = Lookup.getDefault().lookupAll(IRepository.class);
        for (IRepository c : list) {
            providers.put(c.getClass().getSimpleName(), c);
        }
        sort();
    }

    public static IRepository parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

}
