package de.wiwie.wiutils.utils.parse;

import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.utils.ProgressPrinter;
import de.wiwie.wiutils.utils.StringExt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class TextFileParser.
 */
public abstract class TextFileParser {

    /**
     * The Enum OUTPUT_MODE.
     */
    public enum OUTPUT_MODE {

        /** The STREAM. */
        STREAM,
        /** The BURST. */
        BURST
    }

    /** The absolute file path. */
    protected String absoluteFilePath;

    /** The output file. */
    protected String outputFile;

    /** The output mode. */
    protected OUTPUT_MODE outputMode;

    /** The file reader. */
    protected BufferedReader fileReader;

    /** The output writer. */
    private BufferedWriter outputWriter;

    protected boolean terminated = false;

    /** The total line count. */
    protected long totalLineCount = -1;

    /** The columns in file. */
    protected int columnsInFile = -1;

    /** The percent. */
    protected long currentLine = -1, percent = 0;

    protected long parsedLines = 0;

    /** The progress. */
    protected ProgressPrinter progress;

    /** The value columns. */
    protected int[] keyColumns, valueColumns;
    // if this is set to false, values[0] will contain the whole line
    /** The created lock file. */
    protected boolean // createdLockFile,
            exclusiveLockOnTargetFile,
            skipEmptyLines, splitLines;

    /** The out split. */
    protected String inSplit, outSplit;

    /** The key. */
    protected String[] key;

    /** The value. */
    protected String[] value;

    /** The log. */
    protected Logger log;

    protected boolean parsingComments;

    /**
     * Is used to determine whether a line is a comment
     */
    protected String attributeLinePrefix = "//";

    /**
     * The pattern build from the attributeLinePrefix
     */
    protected Pattern attributeLinePrefixPattern = Pattern.compile("\\s*"
            + attributeLinePrefix + ".*");

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                    the abs file path
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath) throws IOException {
        this(absFilePath, null, null);
    }

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                       the abs file path
     * @param keyColumnIds
     *                       the key column ids
     * @param valueColumnIds
     *                       the value column ids
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath, final int[] keyColumnIds,
            final int[] valueColumnIds) throws IOException {
        this(absFilePath, keyColumnIds, valueColumnIds, null, null);
    }

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                       the abs file path
     * @param keyColumnIds
     *                       the key column ids
     * @param valueColumnIds
     *                       the value column ids
     * @param splitLines
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath, final int[] keyColumnIds,
            final int[] valueColumnIds, final boolean splitLines)
            throws IOException {
        this(absFilePath, keyColumnIds, valueColumnIds, splitLines, null, null,
                null);
    }

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                       the abs file path
     * @param keyColumnIds
     *                       the key column ids
     * @param valueColumnIds
     *                       the value column ids
     * @param splitLines
     * @param splitChar
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath, final int[] keyColumnIds,
            final int[] valueColumnIds, final boolean splitLines,
            final String splitChar) throws IOException {
        this(absFilePath, keyColumnIds, valueColumnIds, splitLines, splitChar,
                null, null);
    }

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                       the abs file path
     * @param keyColumnIds
     *                       the key column ids
     * @param valueColumnIds
     *                       the value column ids
     * @param outputFile
     *                       the output file
     * @param outputMode
     *                       the output mode
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath, final int[] keyColumnIds,
            final int[] valueColumnIds, final String outputFile,
            final OUTPUT_MODE outputMode) throws IOException {
        this(absFilePath, keyColumnIds, valueColumnIds, true, null, outputFile,
                outputMode);
    }

    /**
     * Instantiates a new text file parser.
     *
     * @param absFilePath
     *                       the abs file path
     * @param keyColumnIds
     *                       the key column ids
     * @param valueColumnIds
     *                       the value column ids
     * @param splitLines
     *                       the split lines
     * @param splitChar
     * @param outputFile
     *                       the output file
     * @param outputMode
     *                       the output mode
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser(final String absFilePath, final int[] keyColumnIds,
            final int[] valueColumnIds, final boolean splitLines,
            final String splitChar, final String outputFile,
            final OUTPUT_MODE outputMode) throws IOException {
        super();
        this.log = LoggerFactory.getLogger(this.getClass());
        this.splitLines = splitLines;
        this.setFile(absFilePath);
        if (this.splitLines) {
            if (splitChar != null) {
                this.inSplit = splitChar;
            } else {
                this.tryFindSplit();
            }
        }
        if (outputFile != null) {
            this.outputFile = outputFile;
            this.outputMode = outputMode;
            this.outputWriter = new BufferedWriter(new FileWriter(new File(
                    this.outputFile)));
        }

        // by default we assume the file has only one column, in which the value
        // is contained
        if (keyColumnIds == null) {
            this.keyColumns = new int[0];
        } else {
            this.keyColumns = keyColumnIds;
        }

        if (valueColumnIds == null) {
            this.valueColumns = new int[]{0};
        } else {
            this.valueColumns = valueColumnIds;
        }
        // this.totalLineCount = this.getTotalLineCount();
        this.progress = new ProgressPrinter(this.totalLineCount, true, "");
    }

    /**
     * Check for output.
     *
     * @param key
     *              the key
     * @param value
     *              the value
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    private void checkForOutput(final String[] key, final String[] value)
            throws IOException {
        String output = null;
        if ((output = this.getLineOutput(key, value)) != null) {
            this.parsedLines++;
            this.outputWriter.write(output);
        }
    }

    /**
     * Close streams.
     *
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    protected void closeStreams() throws IOException {
        if (this.fileReader != null) {
            this.fileReader.close();
            this.fileReader = null;
        }
        if (this.outputFile != null) {
            if (this.outputWriter != null) {
                this.outputWriter.close();
                this.outputWriter = null;
            }
        }
    }

    /**
     * Combine columns.
     *
     * @param keyColumnEntries
     *                         the key column entries
     * @return the string
     */
    protected String combineColumns(final String... keyColumnEntries) {
        final StringBuffer sb = new StringBuffer();
        for (final String s : keyColumnEntries) {
            sb.append(s + this.outSplit);
        }
        if (sb.toString().length() > 0) {
            sb.deleteCharAt(sb.toString().length() - 1);
        }
        return sb.toString();
    }

    /**
     * Count lines.
     *
     * @return the long
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    protected long countLines() throws IOException {
        this.resetReader();
        log.trace("Counting number of lines in input file...");
        long count = 0;
        try {
            String line = null;
            while ((line = this.fileReader.readLine()) != null) {
                if (!line.isEmpty() || this.skipEmptyLines) {
                    count++;
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.totalLineCount = count;
        log.trace("File contains " + this.totalLineCount + " lines");
        return this.totalLineCount;
    }

    /**
     * Gets the total line count.
     *
     * @return the total line count
     */
    public long getTotalLineCount() {
        if (this.totalLineCount == -1) {
            try {
                this.countLines();
            } catch (IOException e) {
            }
        }
        this.progress = new ProgressPrinter(this.totalLineCount, true, "");
        return this.totalLineCount;
    }

    /**
     * Gets the columns in file.
     *
     * @return the columns in file
     */
    protected int getColumnsInFile() {
        if (this.columnsInFile == -1) {
            try {
                this.countColumnsInFile();
            } catch (IOException e) {
            }
        }
        return this.columnsInFile;
    }

    /**
     * Count columns in file.
     *
     * @return the int
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    private int countColumnsInFile() throws IOException {
        this.resetReader();
        log.trace("Counting columns in input file...");
        int count = 0;
        try {
            String line = this.readLine();
            while (line.isEmpty() && this.skipEmptyLines || !checkLine(line)) {
                line = this.readLine();
            }
            int pos = 0;
            while ((pos = line.indexOf(this.inSplit, pos)) > -1) {
                count++;
                pos++;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // if we found the splitstring at least ones, we have at least two
        // columns
        if (count > 0) {
            count++;
        }
        this.columnsInFile = count;
        log.trace("File contains " + this.columnsInFile + " columns");
        return this.columnsInFile;
    }

    /**
     * Sets the total line count.
     *
     * @param lineCount
     *                  the new total line count
     */
    protected void setTotalLineCount(long lineCount) {
        this.totalLineCount = lineCount;
    }

    /**
     * Extract key entries.
     *
     * @param lineSplit
     *                  the line split
     * @return the string[]
     */
    protected String[] extractKeyEntries(final String[] lineSplit) {
        final String[] keyEntries = new String[this.keyColumns.length];

        int pos = 0;
        for (int x = 0; x < this.keyColumns.length; x++) {
            final int i = this.keyColumns[x];
            keyEntries[pos++] = lineSplit[i];
        }
        return keyEntries;
    }

    /**
     * Extract value entries.
     *
     * @param lineSplit
     *                  the line split
     * @return the string[]
     */
    protected String[] extractValueEntries(final String[] lineSplit) {
        final String[] valueEntries;
        // if the user didn't define any key or value columns we just use all
        // columns as values
        if (this.keyColumns.length == 0 && this.valueColumns.length == 0) {
            valueEntries = lineSplit;
        } else {
            valueEntries = new String[this.valueColumns.length];

            int pos = 0;
            for (int x = 0; x < this.valueColumns.length; x++) {
                final int i = this.valueColumns[x];
                valueEntries[pos++] = lineSplit[i];
            }
        }
        return valueEntries;
    }

    /**
     * This method determines, whether a line is processed or not. By default no
     * filtering takes place and it returns true always. If you want to filter,
     * you have to override this method in your subclasses.
     *
     * @param line
     *             The line to check for filtering.
     * @return True if the line has to be processed, false otherwise.
     */
    protected boolean checkLine(final String line) {
        return true;
    }

    /**
     * Finish process.
     */
    public void finishProcess() {
        // // free lock file
        // if (this.createdLockFile
        // && new File(this.absoluteFilePath + ".lock").exists()) {
        // new File(this.absoluteFilePath + ".lock").delete();
        // }
        this.currentLine = -1;
        this.percent = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * Gets the absolute file path.
     *
     * @return the absoluteFilePath
     */
    public String getAbsoluteFilePath() {
        return this.absoluteFilePath;
    }

    /**
     * Gets the line output.
     *
     * @param key
     *              the key
     * @param value
     *              the value
     * @return the line output
     */
    protected String getLineOutput(final String[] key, final String[] value) {
        // by default write the input

        StringBuilder sb = new StringBuilder();
        sb.append(this.combineColumns(key));
        sb.append(this.inSplit);
        sb.append(this.combineColumns(value));
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    /**
     * Inits the process.
     *
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public void initProcess() throws IOException {
        // TODO: replaced lock-file by common files that are synchronized
        // this.createdLockFile = false;
        // // wait until file is free
        // if (this.exclusiveLockOnTargetFile) {
        // while (new File(this.absoluteFilePath + ".lock").exists()) {
        // log.debug("Waiting for input file to be released...");
        // // dont set lower than 10seconds, too much load for
        // // computing cluster
        // try {
        // Thread.sleep(1000);
        // } catch (final InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // final File lockFile = new File(this.absoluteFilePath + ".lock");
        // if (lockFile.createNewFile()) {
        // this.createdLockFile = true;
        // lockFile.deleteOnExit();
        // }
        // }
    }

    /**
     * Checks if is locking target file.
     *
     * @return true, if is locking target file
     */
    public boolean isLockingTargetFile() {
        return this.exclusiveLockOnTargetFile;
    }

    /**
     * Process.
     *
     * @return the text file parser
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public TextFileParser process() throws IOException {
        this.initProcess();
        // 04.10.2012: introduced new locking mechanism
        File targetFile;
        if (this.exclusiveLockOnTargetFile) {
            targetFile = FileUtils
                    .getCommonFile(new File(this.absoluteFilePath));
        } else {
            targetFile = new File(this.absoluteFilePath);
        }

        synchronized (targetFile) {
            log.debug("Parsing " + this.absoluteFilePath);

            if (this.outputFile != null) {
                log.debug("Output will be written to " + this.outputFile);
            }
            this.resetReader();
            String line = null;
            long lineNumber = 0;
            this.parsingComments = true;
            while ((line = this.readLine()) != null && !this.terminated) {
                try {
                    this.currentLine++;
                    // skip empty lines
                    if (line.isEmpty() && this.skipEmptyLines
                            || !checkLine(line)) {
                        continue;
                    }
                    String[] lineSplit = null;
                    try {
                        // lineSplit = (this.splitLines ? StringExt.split(line,
                        // this.inSplit) : new String[]{line});
                        lineSplit = (this.splitLines
                                     ? line.split(this.inSplit)
                                     : new String[]{line});
                    } catch (NullPointerException e) {
                        System.out.println("test");
                    }
                    final String[] keyEntries = this
                            .extractKeyEntries(lineSplit);
                    final String[] valueEntries = this
                            .extractValueEntries(lineSplit);
                    this.key = keyEntries;// this.combineColumns(keyEntries);
                    this.value = valueEntries;// this.combineColumns(valueEntries);
                    this.processLine(this.key, this.value);
                    if (this.outputFile != null
                            && this.outputMode.equals(OUTPUT_MODE.STREAM)) {
                        this.checkForOutput(this.key, this.value);
                    }

                    if (this.progress.getUpperLimit() > -1L) {
                        this.progress.update(lineNumber + 1);
                    }
                    lineNumber++;
                } catch (IndexOutOfBoundsException e) {
                    log.error("Error while parsing line " + currentLine
                            + " of file " + this.absoluteFilePath);
                    throw e;
                }
            }
            if (this.outputFile != null
                    && this.outputMode.equals(OUTPUT_MODE.BURST)) {
                this.checkForBurstOutput();
            }

            // TODO check whether this has negative effects: changed order from
            // closeStreams,finishProcess to finishProcess,CloseStreams,
            // 14.04.2012
            this.finishProcess();

            this.closeStreams();
        }
        log.debug("Finished parsing " + this.absoluteFilePath);
        return this;
    }

    /**
     * Process line.
     *
     * @param key
     *              the key
     * @param value
     *              the value
     */
    protected abstract void processLine(String[] key, String[] value);

    /**
     * Process burst output.
     *
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    private void checkForBurstOutput() throws IOException {
        String output = null;
        if (!(output = this.getBurstOutput()).isEmpty()) {
            this.outputWriter.write(output);
        }
    }

    protected String getBurstOutput() {
        // by default we do nothing
        return "";
    }

    /**
     * Read line.
     *
     * @return the string
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public String readLine() throws IOException {
        return this.fileReader.readLine();
    }

    /**
     * Reset reader.
     *
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    protected void resetReader() throws IOException {
        if (this.fileReader != null) {
            this.fileReader.close();
        }
        this.fileReader = new BufferedReader(new FileReader(new File(
                this.absoluteFilePath)));
        if (this.outputFile != null) {
            if (this.outputWriter != null) {
                this.outputWriter.close();
            }
            this.outputWriter = new BufferedWriter(new FileWriter(new File(
                    this.outputFile)));
        }
    }

    /**
     * Sets the lock target file.
     *
     * @param lock
     *             the new lock target file
     */
    public void setLockTargetFile(final boolean lock) {
        this.exclusiveLockOnTargetFile = lock;
    }

    /**
     * Sets the output file.
     *
     * @param outFile
     *                the new output file
     */
    public void setOutputFile(final String outFile) {
        this.outputFile = outFile;
    }

    /**
     * Gets the output file.
     *
     * @return
     *
     */
    public String getOutputFile() {
        return this.outputFile;
    }

    /**
     * Sets the output mode.
     *
     * @param outMode
     *                the new output mode
     */
    public void setOutputMode(final OUTPUT_MODE outMode) {
        this.outputMode = outMode;
    }

    /**
     * Sets the file.
     *
     * @param absFilePath
     *                    the new file
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public void setFile(final String absFilePath) throws IOException {
        this.absoluteFilePath = absFilePath;
        this.resetReader();
        if (new File(this.absoluteFilePath).length() == 0) {
            throw new IOException("Empty file given: " + this.absoluteFilePath);
        }
        if (this.splitLines) {
            this.tryFindSplit();
            this.resetReader();
        }

    }

    /**
     * Sets the skip empty lines.
     *
     * @param skipEmptyLines
     *                       the new skip empty lines
     */
    public void setSkipEmptyLines(final boolean skipEmptyLines) {
        this.skipEmptyLines = skipEmptyLines;
    }

    /**
     * Sets the split char.
     *
     * @param split
     *              the new split char
     */
    public void setSplitChar(final String split) {
        this.inSplit = split;
    }

    /**
     * Skip empty lines.
     *
     * @return true, if successful
     */
    public boolean skipEmptyLines() {
        return this.skipEmptyLines;
    }

    /**
     *
     */
    public void terminate() {
        this.terminated = true;
    }

    /**
     * Try find split.
     *
     * @throws IOException
     *                     Signals that an I/O exception has occurred.
     */
    public void tryFindSplit() throws IOException {
        this.parsingComments = true;
        this.outSplit = "\t";
        if (this.inSplit != null) {
            return;
        }
        String line = this.readLine();
        while (line.isEmpty() && this.skipEmptyLines || !checkLine(line)) {
            line = this.readLine();
        }

        if (StringExt.split(line, "\t").length > 1) {
            this.inSplit = "\t";
        } else if (StringExt.split(line, " ").length > 1) {
            this.inSplit = " ";
        } else {
            throw new IOException(
                    "Unknown split character. Please set it manually.");
        }
        this.parsingComments = true;
    }

}
