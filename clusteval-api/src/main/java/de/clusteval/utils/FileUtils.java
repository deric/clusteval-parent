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
package de.clusteval.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christian Wiwie
 *
 */
public class FileUtils {

    protected static Map<File, File> lockedFiles = new ConcurrentHashMap<>();

    public static File getCommonFile(final File file) {
        if (lockedFiles.containsKey(file)) {
            return lockedFiles.get(file);
        }
        lockedFiles.put(file, file);
        return file;
    }

    public static void delete(final File f) {
        if (f.isDirectory()) {
            for (final File sub : f.listFiles()) {
                delete(sub);
            }
        }
        f.delete();
    }

    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (String child : children) {
                copyDirectory(new File(sourceLocation, child), new File(targetLocation, child));
            }
        } else {

            OutputStream out;
            try (InputStream in = new FileInputStream(sourceLocation)) {
                out = new FileOutputStream(targetLocation);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            out.close();
        }
    }

    /**
     * This method appends a string to a textfile
     *
     * @param path
     *               A path to a file
     * @param output
     *               A text to write to a file
     * @throws IOException
     */
    public static void appendStringToFile(final String path, final String output) {
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.append(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method appends a string to a textfile
     *
     * @param path
     *               A path to a file
     * @param output
     *               A text to write to a file
     */
    public static void writeStringToFile(final String path, final String output) {
        try (FileWriter fw = new FileWriter(path, false)) {
            fw.append(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method reads a string from a textfile
     *
     * @param path
     *             A path to a file
     * @return The contents of the file as string.
     */
    public static String readStringFromFile(final String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (br.ready()) {
                sb.append(br.readLine());
                sb.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Builds a file path by concatenating the single strings and separating
     * them with the system specific file separator character.
     *
     * @param strings
     * @return
     */
    public static String buildPath(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (sb.length() != 0) {
                sb.append(System.getProperty("file.separator"));
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
