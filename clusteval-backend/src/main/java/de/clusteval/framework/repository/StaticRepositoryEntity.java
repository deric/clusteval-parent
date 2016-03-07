/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StaticRepositoryEntity<T extends RepositoryObject>
		extends
			RepositoryEntity<T> {

	protected StaticRepositoryEntity<T> parent;

	/**
	 * A map containing all datasets registered in this repository.
	 */
	protected Map<T, T> objects;
	protected Map<String, T> nameToObject;

	public StaticRepositoryEntity(final Repository repository,
			final StaticRepositoryEntity<T> parent, final String basePath) {
		super(repository, basePath);
		this.parent = parent;
		this.objects = new HashMap<T, T>();
		this.nameToObject = new HashMap<String, T>();
	}

	public Collection<T> asCollection() {

		synchronized (this.objects) {
			Collection<T> result = new HashSet<T>(this.objects.values());

			if (parent != null)
				result.addAll(parent.asCollection());

			return result;
		}
	}

	/**
	 * 
	 * This method looks up and returns (if it exists) the data configuration
	 * with the given name.
	 * 
	 * @param search
	 * @return
	 */
	public T findByString(final String search) {
		T result = nameToObject.get(search);
		if (result != null)
			return result;
		if (parent != null)
			return parent.findByString(search);
		return null;
	}

	/**
	 * This method checks, whether there is an object registered, that is equal
	 * to the passed object and returns it.
	 * 
	 * <p>
	 * Equality is checked in terms of
	 * <ul>
	 * <li><b>object.hashCode == other.hashCode</b></li>
	 * <li><b>object.equals(other)</b></li>
	 * </ul>
	 * since internally the repository uses hash datastructures.
	 * 
	 * <p>
	 * By default the {@link RepositoryObject#equals(Object)} method is only
	 * based on the absolute path of the repository object and the repositories
	 * of the two objects, this means two repository objects are considered the
	 * same if they are stored in the same repository and they have the same
	 * absolute path.
	 * 
	 * @param obj
	 * @return
	 * 
	 */
	public <S extends T> S getRegisteredObject(final S obj) {
		return this.getRegisteredObject(obj, true);
	}

	public <S extends T> S getRegisteredObject(final S object,
			final boolean ignoreChangeDate) {
		// get object without changedate

		synchronized (this.objects) {
			S other = (S) this.objects.get(object);
			// inserted parent, 02.06.2012
			if (other == null && parent != null)
				return parent.getRegisteredObject(object, ignoreChangeDate);
			else if (ignoreChangeDate || other == null)
				return other;
			else if (other.changeDate == object.changeDate) {
				return other;
			}
			return object;
		}
	}

	/**
	 * 
	 * This method registers a new object.
	 * 
	 * <p>
	 * First by invoking {@link #getRegisteredObject(RepositoryObject)} the
	 * method checks, whether another object equalling the new object has been
	 * registered before.
	 * 
	 * <p>
	 * If there is no old equalling object, the new object is simply registered
	 * at the repository.
	 * 
	 * <p>
	 * If there is an old equalling object, their <b>changedates</b> are
	 * compared. The new object is only registered, if the changedate of the new
	 * object is newer than the changedate of the old object. If the changedate
	 * is newer, the new object is registered at the repository and a
	 * {@link RepositoryReplaceEvent} is being thrown. This event tells the old
	 * object and all its listeners in {@link RepositoryObject#listener}, that
	 * it has been replaced by the new object. This allows all objects to update
	 * their references to the old object to the new object.
	 * 
	 * <p>
	 * The method also tells the {@link #repository.sqlCommunicator} of the repository, that a
	 * new object has been registered and causes him, to handle the new object.
	 * 
	 * @param object
	 * @return
	 * @throws RegisterException
	 */
	@Override
	public <S extends T> boolean register(final S object)
			throws RegisterException {
		S old = this.getRegisteredObject(object);
		if (old != null) {
			return this.registerWhenExisting(old, object);
		}
		return this.registerWithoutExisting(object);
	}

	protected <S extends T> boolean registerWhenExisting(final S old,
			final S object) throws RegisterException {
		// check, whether the changeDate is equal
		if (old.changeDate >= object.changeDate)
			return false;

		if (this.printOnRegister)
			this.repository.info(object.getClass().getSimpleName() + " "
					+ object.toString() + " reloaded");

		/*
		 * replace old object by new object
		 */
		RepositoryReplaceEvent event = new RepositoryReplaceEvent(old, object);
		synchronized (this.objects) {
			this.objects.put(object, object);
			this.nameToObject.put(object.toString(), object);
			this.repository.pathToRepositoryObject.put(object.absPath, object);
			old.notify(event);

			this.repository.sqlCommunicator.register(object, true);

			return true;
		}
	}

	protected <S extends T> boolean registerWithoutExisting(final S object) {
		synchronized (this.objects) {
			this.objects.put(object, object);
			this.nameToObject.put(object.toString(), object);
			this.repository.pathToRepositoryObject.put(object.absPath, object);
			if (this.printOnRegister)
				this.repository.info("New " + object.getClass().getSimpleName()
						+ ": " + object.toString());

			this.repository.sqlCommunicator.register(object, false);

			return true;
		}
	}

	/**
	 * 
	 * This method unregisters the passed object.
	 * 
	 * <p>
	 * If the object has been registered before and was unregistered now, this
	 * method tells the sql communicator such that he can also handle the
	 * removal of the object.
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public <S extends T> boolean unregister(final S object) {
		synchronized (this.objects) {
			boolean result = this.objects.remove(object) != null;
			result &= this.nameToObject.remove(object.toString()) != null;
			result &= this.repository.pathToRepositoryObject
					.remove(object.absPath) != null;
			if (result) {
				this.unregisterAfterRemove(object);
			}
			return result;
		}
	}

	protected <S extends T> void unregisterAfterRemove(final S object) {
		if (this.printOnRegister)
			this.repository.info(object.getClass().getSimpleName()
					+ " removed: " + object);

		this.repository.sqlCommunicator.unregister(object);
	}
}