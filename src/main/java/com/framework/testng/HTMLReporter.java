package com.framework.testng;

import org.apache.velocity.VelocityContext;
import org.testng.*;
import org.testng.xml.XmlSuite;
import org.uncommons.reportng.AbstractReporter;
import org.uncommons.reportng.ReportNGException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HTMLReporter extends AbstractReporter
{
    private static final String FRAMES_PROPERTY = "org.uncommons.reportng.frames";
    private static final String ONLY_FAILURES_PROPERTY = "org.uncommons.reportng.failures-only";

    private static final String TEMPLATES_PATH = "reportng/templates/html/";
    private static final String INDEX_FILE = "index.html";
    private static final String SUITES_FILE = "suites.html";
    private static final String OVERVIEW_FILE = "overview.html";
    private static final String GROUPS_FILE = "groups.html";
    private static final String RESULTS_FILE = "results.html";
    private static final String CHRONOLOGY_FILE = "chronology.html";
    private static final String OUTPUT_FILE = "output.html";
    private static final String CUSTOM_STYLE_FILE = "custom.css";

    private static final String SUITE_KEY = "suite";
    private static final String SUITES_KEY = "suites";
    private static final String GROUPS_KEY = "groups";
    private static final String RESULT_KEY = "result";
    private static final String FAILED_CONFIG_KEY = "failedConfigurations";
    private static final String SKIPPED_CONFIG_KEY = "skippedConfigurations";
    private static final String FAILED_TESTS_KEY = "failedTests";
    private static final String SKIPPED_TESTS_KEY = "skippedTests";
    private static final String PASSED_TESTS_KEY = "passedTests";
    private static final String METHODS_KEY = "methods";
    private static final String ONLY_FAILURES_KEY = "onlyReportFailures";

    private static final String REPORT_DIRECTORY = "html";

    private static final Comparator<ITestNGMethod> METHOD_COMPARATOR = new TestMethodComparator();
    private static final Comparator<ITestResult> RESULT_COMPARATOR = new TestResultComparator();
    private static final Comparator<IClass> CLASS_COMPARATOR = new TestClassComparator();

    public HTMLReporter()
    {
        super(TEMPLATES_PATH);
    }


    /**
     * Generates a set of HTML files that contain data about the outcome of
     * the specified test suites.
     * @param suites Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    public void generateReport(List<XmlSuite> xmlSuites,
                               List<ISuite> suites,
                               String outputDirectoryName)
    {
        Collections.sort(suites, new Comparator<ISuite>() {
            public int compare(ISuite s1, ISuite s2) {
                return (int)((long)(s1.getAttribute("timestamp")) - (long)(s2.getAttribute("timestamp")));
            }
        });
        removeEmptyDirectories(new File(outputDirectoryName));

        boolean useFrames = System.getProperty(FRAMES_PROPERTY, "true").equals("true");
        boolean onlyFailures = System.getProperty(ONLY_FAILURES_PROPERTY, "false").equals("true");

        File outputDirectory = new File(outputDirectoryName, REPORT_DIRECTORY);
        outputDirectory.mkdirs();

        try
        {
            if (useFrames)
            {
                createFrameset(outputDirectory);
            }
            createOverview(suites, outputDirectory, !useFrames, onlyFailures);
            createSuiteList(suites, outputDirectory, onlyFailures);
            createGroups(suites, outputDirectory);
            createResults(suites, outputDirectory, onlyFailures);
            // Chronology disabled until I figure out how to make it less nonsensical.
            //createChronology(suites, outputDirectory);
            createLog(outputDirectory, onlyFailures);
            copyResources(outputDirectory);
        }
        catch (Exception ex)
        {
            throw new ReportNGException("Failed generating HTML report.", ex);
        }
    }


    /**
     * Create the index file that sets up the frameset.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createFrameset(File outputDirectory) throws Exception
    {
        VelocityContext context = createContext();
        generateFile(new File(outputDirectory, INDEX_FILE),
                INDEX_FILE + TEMPLATE_EXTENSION,
                context);
    }


    private void createOverview(List<ISuite> suites,
                                File outputDirectory,
                                boolean isIndex,
                                boolean onlyFailures) throws Exception
    {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        context.put(ONLY_FAILURES_KEY, onlyFailures);
        generateFile(new File(outputDirectory, isIndex ? INDEX_FILE : OVERVIEW_FILE),
                OVERVIEW_FILE + TEMPLATE_EXTENSION,
                context);
    }


    /**
     * Create the navigation frame.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createSuiteList(List<ISuite> suites,
                                 File outputDirectory,
                                 boolean onlyFailures) throws Exception
    {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        context.put(ONLY_FAILURES_KEY, onlyFailures);
        generateFile(new File(outputDirectory, SUITES_FILE),
                SUITES_FILE + TEMPLATE_EXTENSION,
                context);
    }


    /**
     * Generate a results file for each test in each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createResults(List<ISuite> suites,
                               File outputDirectory,
                               boolean onlyShowFailures) throws Exception
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            int index2 = 1;
            for (ISuiteResult result : suite.getResults().values())
            {
                boolean failuresExist = result.getTestContext().getFailedTests().size() > 0
                        || result.getTestContext().getFailedConfigurations().size() > 0;
                if (!onlyShowFailures || failuresExist)
                {
                    VelocityContext context = createContext();
                    context.put(RESULT_KEY, result);
                    context.put(FAILED_CONFIG_KEY, getByTestClass(result.getTestContext().getFailedConfigurations()));
                    context.put(SKIPPED_CONFIG_KEY, getByTestClass(result.getTestContext().getSkippedConfigurations()));
                    context.put(FAILED_TESTS_KEY, getByTestClass(result.getTestContext().getFailedTests()));
                    context.put(SKIPPED_TESTS_KEY, getByTestClass(result.getTestContext().getSkippedTests()));
                    context.put(PASSED_TESTS_KEY, getByTestClass(result.getTestContext().getPassedTests()));
                    String fileName = String.format("suite%d_test%d_%s", index, index2, RESULTS_FILE);
                    generateFile(new File(outputDirectory, fileName),
                            RESULTS_FILE + TEMPLATE_EXTENSION,
                            context);
                }
                ++index2;
            }
            ++index;
        }
    }

    /**
     * Group test methods by class.
     */
    private Map<IClass, List<ITestResult>> getByTestClass(IResultMap results)
    {
        Map<IClass, List<ITestResult>> resultsMap = new TreeMap<IClass, List<ITestResult>>(CLASS_COMPARATOR);
        for (ITestResult result : results.getAllResults())
        {
            List<ITestResult> resultsForClass = resultsMap.get(result.getTestClass());
            if (resultsForClass == null)
            {
                resultsForClass = new ArrayList<ITestResult>();
                resultsMap.put(result.getTestClass(), resultsForClass);
            }
            int index = Collections.binarySearch(resultsForClass, result, RESULT_COMPARATOR);
            if (index < 0)
            {
                index = -index-1;
            }
            resultsForClass.add(index, result);
        }
        return resultsMap;
    }



    /**
     * Generate a groups list for each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createGroups(List<ISuite> suites,
                              File outputDirectory) throws Exception
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            Map<String, Set<ITestNGMethod>> groups = groupsMap(suite.getMethodsByGroups());
            if (!groups.isEmpty())
            {
                VelocityContext context = createContext();
                context.put(SUITE_KEY, suite);
                context.put(GROUPS_KEY, groups);
                String fileName = String.format("suite%d_%s", index, GROUPS_FILE);
                generateFile(new File(outputDirectory, fileName),
                        GROUPS_FILE + TEMPLATE_EXTENSION,
                        context);
            }
            ++index;
        }
    }


    /**
     * Generate a groups list for each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createLog(File outputDirectory, boolean onlyFailures) throws Exception
    {
        if (!Reporter.getOutput().isEmpty())
        {
            VelocityContext context = createContext();
            context.put(ONLY_FAILURES_KEY, onlyFailures);
            generateFile(new File(outputDirectory, OUTPUT_FILE),
                    OUTPUT_FILE + TEMPLATE_EXTENSION,
                    context);
        }
    }

    private Map<String, Set<ITestNGMethod>> groupsMap(Map<String, Collection<ITestNGMethod>> groups)
    {
        Map<String, Set<ITestNGMethod>> groupsMap = new TreeMap<String, Set<ITestNGMethod>>();
        for (Map.Entry<String, Collection<ITestNGMethod>> entry : groups.entrySet())
        {
            Set<ITestNGMethod> methods = new TreeSet<ITestNGMethod>(METHOD_COMPARATOR);
            methods.addAll(entry.getValue());
            groupsMap.put(entry.getKey(), methods);
        }
        return groupsMap;
    }


    /**
     * Reads the CSS and JavaScript files from the JAR file and writes them to
     * the output directory.
     * @param outputDirectory Where to put the resources.
     * @throws IOException If the resources can't be read or written.
     */
    private void copyResources(File outputDirectory) throws IOException
    {
        copyClasspathResource(outputDirectory, "reportng.css", "reportng.css");
        copyClasspathResource(outputDirectory, "reportng.js", "reportng.js");
        copyClasspathResource(outputDirectory, "sorttable.js", "sorttable.js");
        // If there is a custom stylesheet, copy that.
        File customStylesheet = META.getStylesheetPath();

        if (customStylesheet != null)
        {
            if (customStylesheet.exists())
            {
                copyFile(outputDirectory, customStylesheet, CUSTOM_STYLE_FILE);
            }
            else
            {
                // If not found, try to read the file as a resource on the classpath
                // useful when reportng is called by a jarred up library
                InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(customStylesheet.getPath());
                if (stream != null)
                {
                    copyStream(outputDirectory, stream, CUSTOM_STYLE_FILE);
                }
            }
        }
    }
}
