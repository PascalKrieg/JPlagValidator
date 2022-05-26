package ba.kripas.jplag;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JPlagWrapper {
    private Class<?> JPlagOptionsClass;
    private Class<?> JPlagLanguageOptionClass;
    private Class<?> JPlagClass;
    private Class<?> JPlagResultClass;
    private Class<?> JPlagComparisonClass;
    private Class<?> SubmissionClass;

    private final URL jarFile;

    public JPlagWrapper(File jarPath) throws MalformedURLException {
        this.jarFile = jarPath.toURI().toURL();
    }

    public void Load() throws IncompatibleInterface {
        try {
            ClassLoader classLoader = new URLClassLoader( new URL[]{jarFile} );
            JPlagOptionsClass = Class.forName("de.jplag.options.JPlagOptions", true, classLoader);
            JPlagLanguageOptionClass = Class.forName("de.jplag.options.LanguageOption", true, classLoader);
            JPlagClass = Class.forName("de.jplag.JPlag", true, classLoader);
            JPlagResultClass = Class.forName("de.jplag.JPlagResult", true, classLoader);
            JPlagComparisonClass = Class.forName("de.jplag.JPlagComparison", true, classLoader);
            SubmissionClass = Class.forName("de.jplag.Submission", true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IncompatibleInterface();
        }
    }

    public JPlagResultWrapper run(String submissionPath) throws IncompatibleInterface {
        try {
            var JPlagObject = buildJPlag(submissionPath, "C_CPP");

            var runMethod = JPlagClass.getMethod("run");
            var JPlagResult = runMethod.invoke(JPlagObject);

            return buildResult(JPlagResult);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            throw new IncompatibleInterface();
        }
    }

    private Object buildJPlag(String submissionPath, String language) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object JPlagOptions = buildJPlagOptions(submissionPath, language);
        return JPlagClass.getConstructor(JPlagOptionsClass).newInstance(JPlagOptions);
    }

    private Object buildJPlagOptions(String submissionPath, String language) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var languageOption = buildLanguageOption(language);
        var constructor = JPlagOptionsClass.getConstructor(String.class, JPlagLanguageOptionClass);
        return constructor.newInstance(submissionPath, languageOption);
    }

    private Object buildLanguageOption(String language) throws ClassNotFoundException {
        return Enum.valueOf( (Class<Enum>) JPlagLanguageOptionClass, language);
    }

    private JPlagResultWrapper buildResult(Object rawResultObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var getDurationMethod = JPlagResultClass.getMethod("getDuration");
        var getNumberOfSubmissionsMethod = JPlagResultClass.getMethod("getNumberOfSubmissions");
        var getComparisonsMethod = JPlagResultClass.getMethod("getComparisons");

        long duration = (long)getDurationMethod.invoke(rawResultObject);
        int numberOfSubmissions = (int)getNumberOfSubmissionsMethod.invoke(rawResultObject);

        List<?> rawComparisons = (List<?>) getComparisonsMethod.invoke(rawResultObject);
        List<JPlagComparisonWrapper> comparisons = new ArrayList<>();

        for (Object rawComparison : rawComparisons) {
            comparisons.add(buildComparisonObject(rawComparison));
        }

        return new JPlagResultWrapper(comparisons, duration, numberOfSubmissions);
    }

    private JPlagComparisonWrapper buildComparisonObject(Object rawComparisonObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var getSubmissionName = SubmissionClass.getMethod("getName");

        var getFirstSubmission = JPlagComparisonClass.getMethod("getFirstSubmission");
        var firstSubmission = getFirstSubmission.invoke(rawComparisonObject);
        var firstSubmissionName = (String)getSubmissionName.invoke(firstSubmission);

        var getSecondSubmission = JPlagComparisonClass.getMethod("getSecondSubmission");
        var secondSubmission = getSecondSubmission.invoke(rawComparisonObject);
        var secondSubmissionName = (String)getSubmissionName.invoke(secondSubmission);

        var getMaximalSimilarity = JPlagComparisonClass.getMethod("maximalSimilarity");
        var maximalSimilarity = (float)getMaximalSimilarity.invoke(rawComparisonObject);

        var getMinimalSimilarity = JPlagComparisonClass.getMethod("minimalSimilarity");
        var minimalSimilarity = (float)getMinimalSimilarity.invoke(rawComparisonObject);

        var getSimilarity = JPlagComparisonClass.getMethod("similarity");
        var similarity = (float) getSimilarity.invoke(rawComparisonObject);

        return new JPlagComparisonWrapper(firstSubmissionName, secondSubmissionName, maximalSimilarity, minimalSimilarity, similarity);
    }
}
