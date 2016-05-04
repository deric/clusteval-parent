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
package de.clusteval.api.cluster;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class ClusteringEvaluationFactory extends ServiceFactory<ClusteringEvaluation> {

    private static ClusteringEvaluationFactory instance;

    public static ClusteringEvaluationFactory getInstance() {
        if (instance == null) {
            instance = new ClusteringEvaluationFactory();
        }
        return instance;
    }

    private ClusteringEvaluationFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ClusteringEvaluation> list = Lookup.getDefault().lookupAll(ClusteringEvaluation.class);
        for (ClusteringEvaluation c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static ClusteringEvaluation parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static ClusteringEvaluation parseFromString(IRepository repo, String name) throws UnknownProviderException {
        ClusteringEvaluation inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    public static ClusteringEvaluation parseFromString(IRepository repo, String name, ClusteringEvaluationParameters parameters)
            throws UnknownProviderException {
        ClusteringEvaluation inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));

        return inst;
    }

}
