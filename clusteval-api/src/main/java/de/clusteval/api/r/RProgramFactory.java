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
package de.clusteval.api.r;

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
public class RProgramFactory extends ServiceFactory<IRProgram> {

    private static RProgramFactory instance;

    public static RProgramFactory getInstance() {
        if (instance == null) {
            instance = new RProgramFactory();
        }
        return instance;
    }

    private RProgramFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRProgram> list = Lookup.getDefault().lookupAll(IRProgram.class);
        for (IRProgram c : list) {
            providers.put(c.getName(), c);
            providers.put(c.getClass().getSimpleName(), c);
        }
        sort();
    }

    public static IRProgram parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IRProgram parseFromString(IRepository repo, String name) throws UnknownProviderException {
        IRProgram inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    public static List<IRProgram> parseFromString(IRepository repo, String[] names) throws UnknownProviderException {
        List<IRProgram> res = new LinkedList<>();
        for (String name : names) {
            res.add(parseFromString(repo, name));
        }
        return res;
    }
}
