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
package de.clusteval.data.dataset.format;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.Precision;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataPreprocessor;
import de.clusteval.data.distance.DistanceMeasure;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class ConversionInputToStandardConfiguration implements IConversionInputToStandardConfiguration {

    protected static List<IDataPreprocessor> clonePreprocessors(List<IDataPreprocessor> preprocessors) {
        List<IDataPreprocessor> result = new ArrayList<>();

        for (IDataPreprocessor proc : preprocessors) {
            result.add(proc.clone());
        }

        return result;
    }

    protected List<IDataPreprocessor> preprocessorsBeforeDistance;

    protected IDistanceMeasure distanceMeasureAbsoluteToRelative;

    protected List<IDataPreprocessor> preprocessorsAfterDistance;

    protected Precision similarityPrecision;

    /**
     * @param distanceMeasure
     * @param similarityPrecision
     * @param preprocessorsBeforeDistance
     * @param preprocessorsAfterDistance
     *
     */
    public ConversionInputToStandardConfiguration(
            final DistanceMeasure distanceMeasure,
            final Precision similarityPrecision,
            final List<IDataPreprocessor> preprocessorsBeforeDistance,
            final List<IDataPreprocessor> preprocessorsAfterDistance) {
        super();

        this.similarityPrecision = similarityPrecision;
        this.distanceMeasureAbsoluteToRelative = distanceMeasure;
        this.preprocessorsBeforeDistance = preprocessorsBeforeDistance;
        this.preprocessorsAfterDistance = preprocessorsAfterDistance;
    }

    /**
     * The copy constructor for this class.
     *
     * @param other The object to clone.
     */
    public ConversionInputToStandardConfiguration(final ConversionInputToStandardConfiguration other) {
        super();

        this.similarityPrecision = other.similarityPrecision;
        this.distanceMeasureAbsoluteToRelative = other.distanceMeasureAbsoluteToRelative
                .clone();
        this.preprocessorsBeforeDistance = clonePreprocessors(other.preprocessorsBeforeDistance);
        this.preprocessorsAfterDistance = clonePreprocessors(other.preprocessorsAfterDistance);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public ConversionInputToStandardConfiguration clone() {
        return new ConversionInputToStandardConfiguration(this);
    }

    @Override
    public Precision getSimilarityPrecision() {
        return this.similarityPrecision;
    }

    /**
     * @return The distance measure to use during the conversion of absolute to
     *         relative datasets.
     */
    @Override
    public IDistanceMeasure getDistanceMeasureAbsoluteToRelative() {
        return this.distanceMeasureAbsoluteToRelative;
    }

    /**
     * @return The preprocessors to apply to the dataset before it is converted
     *         to pairwise distances/similarities.
     */
    public List<IDataPreprocessor> getPreprocessorsBeforeDistance() {
        return this.preprocessorsBeforeDistance;
    }

    /**
     * @return The preprocessors to apply to the dataset after it is converted
     *         to pairwise distances/similarities.
     */
    public List<IDataPreprocessor> getPreprocessorsAfterDistance() {
        return this.preprocessorsAfterDistance;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConversionInputToStandardConfiguration)) {
            return false;
        }

        ConversionInputToStandardConfiguration other = (ConversionInputToStandardConfiguration) obj;

        return this.distanceMeasureAbsoluteToRelative
                .equals(other.distanceMeasureAbsoluteToRelative)
                && this.similarityPrecision.equals(other.similarityPrecision);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (this.distanceMeasureAbsoluteToRelative.toString() + this.similarityPrecision
                .toString()).hashCode();
    }

}
