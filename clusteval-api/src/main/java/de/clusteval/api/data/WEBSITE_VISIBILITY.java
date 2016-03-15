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

/**
 * This enum describes the visibility of a dataset on the website.
 *
 * @author deric
 */
public enum WEBSITE_VISIBILITY {
    /**
     * This dataset is not visible on the website at all. It is not even
     * listed in the list of all datasets.
     */
    HIDE, /**
     * The dataset is listed under all datasets, but not selected by
     * default.
     */
    SHOW_OPTIONAL,
    /**
     * This dataset is listed and also selected to be shown by default on
     * the website.
     */
    SHOW_ALWAYS
}
