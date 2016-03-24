package de.wiwie.wiutils.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class keeps track of a progress and prints out status information.
 *
 * @author Christian Wiwie
 *
 */
public class ProgressPrinter implements ChangeListener {

    /**
     * A factory method to initialize the attributes of the first progress
     * printer with those of the second.
     *
     * @param progress
     *                        The ProgressPrinter that is to be initialized
     * @param overallProgress
     *                        The values from this ProgressPrinter are copied into the other
     *                        object
     * @return The progress object initialized with new values
     */
    public static ProgressPrinter initWithValuesOf(
            final ProgressPrinter progress,
            final ProgressPrinter overallProgress) {
        progress.ownPos = overallProgress.ownPos;
        progress.linePrefix = overallProgress.linePrefix;
        progress.listener = overallProgress.listener;
        progress.percent = overallProgress.percent;
        progress.optStatus = overallProgress.optStatus;
        progress.printOnNewPercent = overallProgress.printOnNewPercent;
        progress.upperLimit = overallProgress.upperLimit;
        return progress;
    }

    /**
     * The logger which is used to print status information.
     */
    protected Logger log;

    /**
     * The upper limit of the progress in terms of a number of steps to process.
     * If the progress reaches this upper limit, it is finished.
     */
    protected long upperLimit;

    /**
     * This is the current number of steps finished by this progress printer
     * alone, not including progress of any child {@link ProgressPrinter}
     * objects stored in {@link #subProgress}.
     */
    protected long ownPos;

    /**
     * This is the current number of steps finished by this progress and sub
     * progresses.
     */
    protected long currentPos;

    /**
     * The current percentage finished in total. This variable is calculated in
     * {@link #update(long)} and is determined as {@link #currentPos}/
     * {@link #upperLimit}*100.
     */
    protected int percent;

    /**
     * This attribute can be set optionally by passing it to
     * {@link #update(long, String)} and is then printed in brackets behind the
     * current percentage.
     */
    protected String optStatus;

    /**
     * This attribute determines, whether this progress printer should log the
     * status every time a new percentage is reached.
     */
    protected boolean printOnNewPercent;

    /**
     * The ouput lines of this printer are prefixed with the string stored in
     * this attribute.
     */
    protected String linePrefix;

    /**
     * Listeners of this progress printer are notified every time a new
     * percentage is reached.
     */
    protected List<ChangeListener> listener;

    /**
     * A map holding all sub progress printers together with a number of steps
     * that determines, to what extent they influence the overall progress of
     * this progress printer.
     *
     * <p>
     * If this progress printer has a {@link #upperLimit} of 2000, one sub
     * progress printer is added with a number of steps of 1000 and a current
     * percentage of 80% and another one with a number of steps of 1000 and
     * percentage of 60%, then the current step of this progress is calculated
     * as CURRENT_STEP=1000*80%+1000*60%=800+600=1400.
     */
    protected Map<ProgressPrinter, Long> subProgress;

    /**
     * Create a new uninitialized progress printer with fallback parameters. The
     * upper limit is set to -1, {@link #printOnNewPercent} is set to false and
     * the {@link #optStatus} is null.
     *
     * <p>
     * If you do not set {@link #upperLimit} to a value != -1, an exception will
     * be thrown when invoking {@link #update(long, String)}.
     */
    public ProgressPrinter() {
        this(-1l, false, null);
    }

    /**
     * Create a new progress printer.
     *
     * @param upperLimit
     *                          The number of total steps of this progress.
     * @param printOnNewPercent
     *                          Whether to log a status information when a new percentage is
     *                          reached (see {@link #printOnNewPercent}).
     */
    public ProgressPrinter(final long upperLimit, final boolean printOnNewPercent) {
        this(upperLimit, printOnNewPercent, null);
    }

    /**
     *
     * @param upperLimit
     *                          The number of total steps of this progress.
     * @param printOnNewPercent
     *                          Whether to log a status information when a new percentage is
     *                          reached (see {@link #printOnNewPercent}).
     * @param linePrefix
     *                          The prefix of logged lines (see {@link #linePrefix} ).
     */
    public ProgressPrinter(final long upperLimit,
            final boolean printOnNewPercent, final String linePrefix) {
        super();
        this.upperLimit = upperLimit;
        this.listener = new LinkedList<>();
        this.subProgress = new HashMap<>();
        this.printOnNewPercent = printOnNewPercent;
        this.linePrefix = linePrefix;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * The copy constructor of progress printers.
     *
     * @param other
     *              The progress printer to copy.
     */
    public ProgressPrinter(final ProgressPrinter other) {
        this(other.upperLimit, other.printOnNewPercent, other.linePrefix);
    }

    /**
     *
     * @param listener
     *                 The new listener to add.
     */
    public void addChangeListener(final ChangeListener listener) {
        this.listener.add(listener);
    }

    /**
     * @param progress
     *                          The new sub progress of this progress.
     * @param partOfSubProgress
     *                          The weight of the sub progress.
     * @see #subProgress
     */
    public void addSubProgress(final ProgressPrinter progress, final long partOfSubProgress) {
        this.subProgress.put(progress, partOfSubProgress);
        progress.addChangeListener(this);
    }

    /**
     * Notify the listeners of this progress about a change of status.
     */
    protected void fireChangeListener() {
        for (final ChangeListener lis : this.listener) {
            lis.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * @return The current position of this progress.
     * @see #currentPos
     */
    public long getCurrentPos() {
        return this.currentPos;
    }

    /**
     * @return The current percentage of this progress.
     * @see #percent
     */
    public int getPercent() {
        return this.percent;
    }

    /**
     * @return The upper limit of this progress.
     * @see #upperLimit
     */
    public long getUpperLimit() {
        return this.upperLimit;
    }

    /**
     * Resetting a progress printer means to set the current position and
     * finished percentage to 0.
     */
    public void reset() {
        this.currentPos = 0;
        this.ownPos = 0;
        this.percent = 0;
    }

    /**
     *
     * @param linePrefix
     *                   The new line prefix for log outputs.
     * @see #linePrefix
     */
    public void setLinePrefix(final String linePrefix) {
        this.linePrefix = linePrefix;
    }

    /**
     *
     * @param printOnNewPercent
     *                          Whether to log newly reached percentages.
     * @see #printOnNewPercent
     */
    public void setPrintOnNewPercent(final boolean printOnNewPercent) {
        this.printOnNewPercent = printOnNewPercent;
    }

    /**
     *
     * @param upperLimit
     *                   The new upper limit of this progress as a absolute number.
     * @see #upperLimit
     */
    public void setUpperLimit(final long upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * This method is invoked by sub progress printers, this progress printer is
     * listening to. If they change their percentage, this progress printer has
     * to integrate it into the overall progress.
     */
    @Override
    public void stateChanged(@SuppressWarnings("unused") final ChangeEvent e) {
        this.update(this.ownPos);
    }

    /**
     * Invoke this method from outside to update the status of this progress
     * printer and set the number of the current step.
     *
     * @param newCurrent
     *                   The new status as a absolute number.
     */
    public void update(final long newCurrent) {
        this.update(newCurrent, this.optStatus);
    }

    /**
     * Invoke this method from outside to update the status of this progress
     * printer and set the number of the current stept as well as the status
     * that should be printed in logging events behind the current percentage.
     *
     * @param newCurrent
     *                   The new status as a absolute number.
     * @param optStatus
     *                   The new status as a string.
     */
    public void update(final long newCurrent, final String optStatus) {
        if (this.upperLimit == -1l) {
            throw new IllegalArgumentException(
                    "This progress printer has not been initialized");
        }
        this.ownPos = newCurrent;

        // integrate progress of subprogresses
        long sumOfSubProgresses = 0L;
        this.currentPos = 0;
        for (final ProgressPrinter subProgress : this.subProgress.keySet()) {
            double percentFinishedSubProgress = subProgress.getPercent() / 100.0;
            long totalNumberStepsSubProgress = this.subProgress
                    .get(subProgress);
            this.currentPos += (percentFinishedSubProgress * totalNumberStepsSubProgress);
            sumOfSubProgresses += totalNumberStepsSubProgress;
        }
        final long remainingPart = this.upperLimit - sumOfSubProgresses;
        if (remainingPart < 0) {
            throw new IllegalArgumentException(
                    "Invalid part-sums of sub progresses");
        }
        this.currentPos += this.ownPos;

        final int newPercent = this.currentPos > 0
                               ? (int) ((double) (this.currentPos) / this.upperLimit * 100.0)
                               : 0;

        this.optStatus = optStatus;

        // status update
        if (this.upperLimit > 0 && newPercent > this.percent) {
            if (this.printOnNewPercent) {
                while (newPercent > this.percent) {
                    this.percent++;
                    if (this.percent < 100) {
                        log(this.percent
                                + "%"
                                + (this.optStatus != null ? " ["
                                        + this.optStatus + "]" : "") + "\t");
                    } else {
                        log(this.percent
                                + "%"
                                + (this.optStatus != null ? " ["
                                        + this.optStatus + "]" : ""));
                    }
                }
            } else {
                this.percent = newPercent;
            }

            this.fireChangeListener();
        }
    }

    protected void log(final String message) {
        log.debug(message);
    }
}
