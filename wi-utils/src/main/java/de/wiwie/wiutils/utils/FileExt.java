package de.wiwie.wiutils.utils;

import java.io.File;
import java.io.IOException;

import de.wiwie.wiutils.file.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FileExt.
 */
public class FileExt extends File {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5535938984313569667L;
	
	/** The overwrite. */
	private final boolean overwrite;

	/**
	 * Instantiates a new file ext.
	 *
	 * @param pathname the pathname
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FileExt(final String pathname) throws IOException {
		this(pathname, true);
	}

	/**
	 * Instantiates a new file ext.
	 *
	 * @param pathname the pathname
	 * @param overwrite the overwrite
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FileExt(final String pathname, final boolean overwrite)
			throws IOException {
		super(pathname);
		this.overwrite = overwrite;
		if (overwrite) {
			this.createNewFile();
		}
	}

	/* (non-Javadoc)
	 * @see java.io.File#createNewFile()
	 */
	@Override
	public boolean createNewFile() throws IOException {
		return this.createNewFile(true);
	}

	/**
	 * Creates the new file.
	 *
	 * @param forceDirectories the force directories
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean createNewFile(final boolean forceDirectories)
			throws IOException {
		if (forceDirectories) {
			if (!this.getParentFile().exists()
					&& !this.getParentFile().mkdirs()) {
				throw new IOException("Failed to create directories to file "
						+ this.getParentFile());
			}
			if (!this.getParentFile().exists()) {
				throw new IOException("Failed to create directories to file "
						+ this.getParentFile());
			}
		}
		if (this.overwrite && this.exists()) {
			FileUtils.delete(this);
		}
		if (!super.createNewFile()) {
			throw new IOException("Failed to create file "
					+ this.getAbsolutePath());
		}
		return true;
	}
}
