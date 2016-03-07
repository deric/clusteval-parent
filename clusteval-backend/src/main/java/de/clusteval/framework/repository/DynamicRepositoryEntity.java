/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.Rserve.RserveException;

import de.clusteval.framework.RLibraryNotLoadedException;
import de.clusteval.framework.RLibraryRequirement;
import de.clusteval.utils.UnsatisfiedRLibraryException;

public class DynamicRepositoryEntity<T extends RepositoryObject>
		extends
			RepositoryEntity<T> {

	protected static Map<String, Class<? extends RepositoryObject>> loadedClasses = new HashMap<String, Class<? extends RepositoryObject>>();

	protected DynamicRepositoryEntity<T> parent;

	/**
	 * A map containing all objects registered in this entity.
	 */
	protected Map<String, List<T>> objects;
	protected Map<String, Class<? extends T>> classes;

	public DynamicRepositoryEntity(final Repository repository,
			final DynamicRepositoryEntity<T> parent, final String basePath) {
		super(repository, basePath);
		this.parent = parent;
		this.objects = new HashMap<String, List<T>>();
		this.classes = new HashMap<String, Class<? extends T>>();
	}

	public Collection<Class<? extends T>> getClasses() {
		Collection<Class<? extends T>> result = new HashSet<Class<? extends T>>(
				this.classes.values());

		if (parent != null)
			result.addAll(parent.getClasses());

		return result;
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
		S other = null;

		synchronized (this.objects) {
			for (List<T> list : this.objects.values()) {
				for (T elem : list)
					if (elem.equals(object)) {
						other = (S) elem;
						break;
					}
			}
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
	 * This method registers a new class. It is only registered, if it was not
	 * before.
	 * 
	 * @param object
	 *            The new object to register.
	 * @return True, if the new object has been registered.
	 */
	public <S extends T> boolean registerClass(final Class<S> object) {
		if (isClassRegistered(object)) {
			// first remove the old class
			unregisterClass(this.classes.get(object.getName()));
		}
		this.classes.put(object.getName(), object);

		synchronized (this.objects) {
			// is this right, to always put an empty list even though there was
			// an
			// old class before?
			this.objects.put(object.getSimpleName(),
					Collections.synchronizedList(new ArrayList<T>()));

			try {
				if (!ensureLibraries(object))
					return false;
			} catch (InterruptedException e) {
				return false;
			}

			DynamicRepositoryEntity.loadedClasses.put(object.getName(), object);

			this.repository.log.info("Dynamic class registered: "
					+ object.getSimpleName());

			this.repository.sqlCommunicator.register(object);

			return true;
		}
	}

	/**
	 * This method checks whether a class is registered in this repository.
	 * 
	 * @param classToRegister
	 *            The class to look up.
	 * @return True, if the class was registered.
	 */
	public <S extends T> boolean isClassRegistered(
			final Class<S> classToRegister) {
		return this.isClassRegistered(classToRegister.getName());
	}

	/**
	 * This method checks whether a class with the given name is registered in
	 * this repository.
	 * 
	 * @param className
	 *            The name of the class to look up.
	 * @return True, if the class was registered.
	 */
	public boolean isClassRegistered(final String className) {
		return this.classes.containsKey(className)
				|| (this.parent != null && this.parent
						.isClassRegistered(className));
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
		synchronized (this.objects) {
			this.objects.get(object.getClass().getSimpleName()).add(object);
			// TODO: check duplicates in list?
		}
		return true;
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
			boolean result = this.objects
					.get(object.getClass().getSimpleName()).remove(object);
			if (result) {
				try {
					object.notify(new RepositoryRemoveEvent(object));
				} catch (RegisterException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
	}

	/**
	 * This method unregisters the passed object.
	 * 
	 * <p>
	 * If the object has been registered before and was unregistered now, this
	 * method tells the sql communicator such that he can also handle the
	 * removal of the object.
	 * 
	 * @param c
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public <S extends T> boolean unregisterClass(final Class<S> c) {
		boolean result = this.classes.remove(c.getName()) != null;
		if (result) {
			if (this.printOnRegister)
				this.repository.info("Dynamic class removed: "
						+ c.getSimpleName());
			// we inform all listeners about the new class. that
			// means those objects are deleted such that new instances instances
			// can be created using the new class.

			synchronized (this.objects) {
				for (S object : Collections.synchronizedList(new ArrayList<S>(
						(List<S>) objects.get(c.getSimpleName())))) {
					object.unregister();
				}

				DynamicRepositoryEntity.loadedClasses.remove(c.getName());

				this.repository.sqlCommunicator.unregister(c);
			}
		}
		return result;
	}

	/**
	 * This method assumes, that the class that is passed is currently
	 * registered in this repository.
	 * 
	 * <p>
	 * If the R libraries are not satisfied, the class is removed from the
	 * repository.
	 * 
	 * @param classObject
	 *            The class for which we want to ensure R library dependencies.
	 * @return True, if all R library dependencies are fulfilled.
	 * @throws InterruptedException
	 * @throws UnsatisfiedRLibraryException
	 */
	protected <S extends T> boolean ensureLibraries(final Class<S> classObject)
			throws InterruptedException {
		if (classObject.isAnnotationPresent(RLibraryRequirement.class)) {
			String[] requiredLibraries = classObject.getAnnotation(
					RLibraryRequirement.class).requiredRLibraries();

			// ensure that all R libraries are available
			MyRengine rEngine;
			try {
				rEngine = this.repository.getRengineForCurrentThread();

				// ensure that all R libraries are available
				for (String libName : requiredLibraries)
					try {
						rEngine.loadLibrary(libName,
								classObject.getSimpleName());
						// first we clear the old exceptions for this
						// class
						this.repository.clearMissingRLibraries(classObject
								.getName());
					} catch (RLibraryNotLoadedException e) {
						if (this.repository.addMissingRLibraryException(e))
							this.repository
									.warn("\""
											+ classObject.getSimpleName()
											+ "\" could not be loaded due to an unsatisfied R library dependency: "
											+ libName);
						this.classes.remove(classObject.getName());
						return false;
					}
				return true;
			} catch (RserveException e) {
				if (this.repository
						.addMissingRLibraryException(new RLibraryNotLoadedException(
								classObject.getName(), "R")))
					this.repository
							.warn("\""
									+ classObject.getSimpleName()
									+ "\" could not be loaded since it requires R and no connection could be established.");
				this.classes.remove(classObject.getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * This method looks up and returns (if it exists) the class with the given
	 * name.
	 * 
	 * @param className
	 *            The name of the class.
	 * @return The class with the given name or null, if it does not exist.
	 */
	public Class<? extends T> getRegisteredClass(final String className) {
		Class<? extends T> result = this.classes.get(className);
		if (result == null && parent != null)
			result = this.parent.getRegisteredClass(className);
		return result;
	}

	public static boolean isClassAvailable(final String fullClassName) {
		return loadedClasses.containsKey(fullClassName);
	}
}