package de.layclust.taskmanaging;

import java.io.File;

public class TaskConfig {

    // private static Logger log = Logger.getLogger(TaskConfig.class.getName());
    // --------------------- FIXED VARIABLES ---------------------- //
    /* program details */
    public final static String NAME = "TransClust";
    public final static String NAME_EXTENDED = "Clustering by Weighted Transitive Graph Projection";
    public final static String VERSION = "1.0";
//	public final static String[] AUTHORS = {"Sita Lange: sita.lange@cebitec.uni-bielefeld.de",
//		"Nils Kleinboelting: nils.kleinboelting@cebitec.uni-bielefeld.de",
//		"Tobias Wittkop: tobias.wittkop@cebitec.uni-bielefeld.de",
//		"and Jan Baumbach: jan.baumbach@cebitec.uni-bielefeld.de"};
    public final static String[] AUTHORS = {"Tobias Wittkop: tobias.wittkop@cebitec.uni-bielefeld.de",
        " and Jan Baumbach: jan.baumbach@icsi.berkeley.edu"};
    public final static String[] DEVELOPERS = {"Tobias Wittkop: tobias.wittkop@cebitec.uni-bielefeld.de",
        "Jan Baumbach: jan.baumbach@icsi.berkeley.edu", "Sita Lange: sita.lange@cebitec.uni-bielefeld.de", "Nils Kleinboelting: nils.kleinboelting@cebitec.uni-bielefeld.de", "and Dorothea Emig: demig@mpi-inf.mpg.de"};
    public final static String JAR = "TransClust.jar";

    public final static String NL = System.getProperty("line.separator"); //newline
    public final static String TAB = "\t";
    public final static String FS = System.getProperty("file.separator"); //slash

    public final static String DEFAULTCONFIG = "Default.conf";
    public final static int CLUSTERING_MODE = 0;
    public final static int GENERAL_TRAINING_MODE = 1;
    public final static int COMPARISON_MODE = 2;
    public final static int HIERARICHAL_MODE = 3;
    public final static int SYSTEM_NO_AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    //	 --------------------------------------------------------------------------- //
    // --------------------- INPUT/CONFIG VARIABLES ---------------------- //

    /* ---- file paths ---- */
    public static String outConfigPath; // for training mode
    public static String inputConfigPath = DEFAULTCONFIG;
    public static String clustersPath;
    public static String transitiveConnectedComponents;
    public static String cmPath;
    public static String goldstandardPath;
    public static String infoPath;
    public static File tempDir;


    /* ---- general ---- */
    public static boolean useThreads = true;
//	public static boolean useThreadsForCCs= useThreads;
    public static int maxNoThreads = Runtime.getRuntime().availableProcessors() - 1;
    public static String ccEdgesClass = "CC2DArray";
    public static boolean verbose = false;
    public static int mode = CLUSTERING_MODE;
    public static boolean info = false; //default is that no info file is created

    public static boolean gui = false; //if the program is started with the gui or not.
    public static boolean useConfigFile = false;

    /* ---- layouting ----*/
    public static String layouterClasses = "FORCEnDLayouter"; //use correct class names
    public static int dimension = 3;


    /* ---- parameter training for the layouters ---- */
    public static String parameterTrainingClass = "ParameterTraining_SE";
    public static boolean doLayoutParameterTraining = false;
    public static int noOfParameterConfigurationsPerGeneration = 15; //minimum = 2!!
    public static int noOfGenerations = 3; //min number of generations = 1;
//	public static boolean useThreadsForParameterTraining = false;


    /* ---- geometric clustering ---- */
    public static String geometricClusteringClass = "SingleLinkageClusterer";
//	public static String geometricClusteringClass = "KmeansClusterer";


    /* ---- post-processing ---- */
    public static boolean doPostProcessing = true;
    public static String postProcessingClass = "PP_DivideAndReclusterRecursively";

//	/* ---- logging ---- */
    public static boolean setLogLevel = false;
    // public static LogLevel logLevel = LogLevel.LOG_FATAL;

//	/* ---- additional ---- */
    public static double minThreshold = 0;

    public static double thresholdStepSize = 1;

    public static double maxThreshold = 100;

    public static boolean clusterHierarchicalComplete = false;

    public static boolean greedy = false;

    public static boolean fixedParameter = true;

    public static int fixedParameterMax = 20;

    public static long fpMaxTimeMillis = 1000;

    public static boolean fpStopped = false;

    public static float upperBound = Float.MAX_VALUE;

    public static boolean reducedMatrix = false;

    private static boolean debug = false;

    public static boolean developerMode = true;

    public static boolean fuzzy = false;

    public static boolean overlap = false;

    public static double fuzzyThreshold = 0.5;

    public static double lowerBound = 0;

    public static boolean UseLimitK = false;

    public static int limitK = 7;

    public static String knownAssignmentsFile;

//	public static Hashtable<Integer, Vector<Integer>> dummy;
}
