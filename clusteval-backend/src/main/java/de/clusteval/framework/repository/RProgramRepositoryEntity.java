/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.program.Program;
import de.clusteval.program.r.RProgram;
import java.lang.reflect.InvocationTargetException;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * @author Christian Wiwie
 *
 */
public class RProgramRepositoryEntity extends DynamicRepositoryEntity<RProgram> {

    protected StaticRepositoryEntity<Program> programEntity;

    /**
     * @param repository
     * @param programEntity
     * @param parent
     * @param basePath
     */
    public RProgramRepositoryEntity(Repository repository,
            StaticRepositoryEntity<Program> programEntity,
            DynamicRepositoryEntity<RProgram> parent, String basePath) {
        super(repository, parent, basePath);
        this.programEntity = programEntity;
    }

    @Override
    public boolean register(final RProgram rProgram) throws RegisterException {
        programEntity.register(rProgram);
        return super.register(rProgram);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.clusteval.framework.repository.DynamicRepositoryEntity#registerClass
	 * (java.lang.Class)
     */
    @Override
    public <S extends RProgram> boolean registerClass(Class<S> object) {
        if (!super.registerClass(object)) {
            return false;
        }

        try {
            // registers the program
            object.getConstructor(Repository.class)
                    .newInstance(this.repository);
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.clusteval.framework.repository.DynamicRepositoryEntity#ensureLibraries
	 * (java.lang.Class)
     */
    @Override
    protected <S extends RProgram> boolean ensureLibraries(Class<S> classObject)
            throws InterruptedException {
        boolean result;
        try {
            // check whether we have R available
            this.repository.getRengineForCurrentThread();

            result = super.ensureLibraries(classObject);
        } catch (RserveException e) {
            this.repository.warn("\"" + classObject.getSimpleName()
                    + "\" could not be loaded since it requires R and no connection could be established.");
            repository.warn(e.getMessage());
            repository.warn("Is Rserver running on " + ClustevalBackendServer.getBackendServerConfiguration().getRserveHost() + ":" + ClustevalBackendServer.getBackendServerConfiguration().getRservePort() + "?");

            result = false;
        }
        if (!result) {
            this.classes.remove(classObject.getName());
        }
        return result;
    }
}
