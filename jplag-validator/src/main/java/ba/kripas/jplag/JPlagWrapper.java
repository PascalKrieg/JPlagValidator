package ba.kripas.jplag;

import ba.kripas.dataset.Project;
import ba.kripas.running.JarConfig;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JPlagWrapper {
    private ClassLoader classLoader;

    private Class<?> JPlagOptionsClass;
    private Class<?> JPlagLanguageOptionClass;
    private Class<?> JPlagClass;
    private Class<?> JPlagResultClass;
    private Class<?> JPlagComparisonClass;
    private Class<?> SubmissionClass;

    private final List<ConfigSetterContainer> configSettings = new ArrayList<>();

    private final URL jarFile;

    public JPlagWrapper(JarConfig jarConfig) throws MalformedURLException, IncompatibleInterfaceException, InvalidOptionsException {
        this.jarFile = jarConfig.getJarFile().toURI().toURL();
        load();
        loadConfigSetters(jarConfig.getOptionsOverrides());
    }

    private void load() throws IncompatibleInterfaceException {
        try {
            classLoader = new URLClassLoader(new URL[]{jarFile});
            JPlagOptionsClass = Class.forName("de.jplag.options.JPlagOptions", true, classLoader);
            JPlagLanguageOptionClass = Class.forName("de.jplag.options.LanguageOption", true, classLoader);
            JPlagClass = Class.forName("de.jplag.JPlag", true, classLoader);
            JPlagResultClass = Class.forName("de.jplag.JPlagResult", true, classLoader);
            JPlagComparisonClass = Class.forName("de.jplag.JPlagComparison", true, classLoader);
            SubmissionClass = Class.forName("de.jplag.Submission", true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IncompatibleInterfaceException();
        }
    }

    private void loadConfigSetters(Collection<OptionsOverride> optionsOverrides) throws InvalidOptionsException {
        try {
            for (var override : optionsOverrides) {

                var type = override.getType();
                var valueString = override.getValue();

                var parsedValue = parseValueAndType(valueString, type);
                var setter = JPlagOptionsClass.getMethod(override.getSetter(), parsedValue.getClass());

                configSettings.add(new ConfigSetterContainer(setter, parsedValue));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new InvalidOptionsException();
        }
    }

    private Object parseValueAndType(String value, String type) throws InvalidOptionsException {
        try {
            switch (type.toLowerCase()) {
                case "int":
                case "integer":
                    return Integer.parseInt(value);
                case "float":
                    return Float.parseFloat(value);
                case "bool":
                case "boolean":
                    return Boolean.parseBoolean(value); // Note: every value other than "true" is returned as false, even if it is not a bool
            }
        } catch (NumberFormatException e) {
            throw new InvalidOptionsException("Invalid Options: Cannot parse " + type + " (" + value + ")");
        }

        if (!type.toLowerCase().startsWith("enum:"))
            throw new InvalidOptionsException("Unknown type: " + type);

        var enumType = type.substring(5, type.length());
        try {
            var enumClass = Class.forName(enumType, true, classLoader);
            return Enum.valueOf((Class<Enum>) enumClass, value);
        } catch (ClassNotFoundException e) {
            throw new InvalidOptionsException("Unkown enum: " + enumType);
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionsException("Unkown enum value " + value + " for enum type " + enumType);
        }
    }

    public JPlagResultWrapper run(Project targetProject) throws IncompatibleInterfaceException {
        try {
            var languageString = targetProject.getLanguage().getJplagConfigIdentifier();
            var projectPath = targetProject.getPath();

            var JPlagObject = buildJPlag(projectPath, languageString);

            var runMethod = JPlagClass.getMethod("run");
            var JPlagResult = runMethod.invoke(JPlagObject);

            return buildResult(JPlagResult);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            throw new IncompatibleInterfaceException();
        }
    }

    private Object buildJPlag(Path submissionPath, String language) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object JPlagOptions = buildJPlagOptions(submissionPath, language);
        return JPlagClass.getConstructor(JPlagOptionsClass).newInstance(JPlagOptions);
    }

    private Object buildJPlagOptions(Path submissionPath, String language) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var languageOption = buildLanguageOption(language);

        Object optionsInstance;

        // Very hacky way of trying different JPlag API versions
        try {
            var constructor = JPlagOptionsClass.getConstructor(List.class, List.class, JPlagLanguageOptionClass);
            optionsInstance = constructor.newInstance(List.of(submissionPath.toString()), List.of(), languageOption);
        } catch (NoSuchMethodException e) {
            // 3.0.0 API
            var constructor = JPlagOptionsClass.getConstructor(String.class, JPlagLanguageOptionClass);
            optionsInstance = constructor.newInstance(submissionPath.toString(), languageOption);
        }

        for (var setterContainer : configSettings) {
            var setter = setterContainer.getSetter();
            var value = setterContainer.getValue();
            setter.invoke(optionsInstance, value);
        }

        return optionsInstance;
    }

    private Object buildLanguageOption(String language) {
        return Enum.valueOf((Class<Enum>) JPlagLanguageOptionClass, language);
    }

    private JPlagResultWrapper buildResult(Object rawResultObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var getDurationMethod = JPlagResultClass.getMethod("getDuration");
        var getNumberOfSubmissionsMethod = JPlagResultClass.getMethod("getNumberOfSubmissions");
        var getComparisonsMethod = JPlagResultClass.getMethod("getComparisons");

        long duration = (long) getDurationMethod.invoke(rawResultObject);
        int numberOfSubmissions = (int) getNumberOfSubmissionsMethod.invoke(rawResultObject);

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
        var firstSubmissionName = (String) getSubmissionName.invoke(firstSubmission);

        var getSecondSubmission = JPlagComparisonClass.getMethod("getSecondSubmission");
        var secondSubmission = getSecondSubmission.invoke(rawComparisonObject);
        var secondSubmissionName = (String) getSubmissionName.invoke(secondSubmission);

        var getMaximalSimilarity = JPlagComparisonClass.getMethod("maximalSimilarity");
        var maximalSimilarity = (float) getMaximalSimilarity.invoke(rawComparisonObject);

        var getMinimalSimilarity = JPlagComparisonClass.getMethod("minimalSimilarity");
        var minimalSimilarity = (float) getMinimalSimilarity.invoke(rawComparisonObject);

        var getSimilarity = JPlagComparisonClass.getMethod("similarity");
        var similarity = (float) getSimilarity.invoke(rawComparisonObject);

        boolean isSuspicious = false;
        try {
            var getIsSuspicious = JPlagComparisonClass.getMethod("isSuspicious");
            isSuspicious = (boolean) getIsSuspicious.invoke(rawComparisonObject);
        } catch (NoSuchMethodException ignored) { }

        return new JPlagComparisonWrapper(firstSubmissionName, secondSubmissionName, maximalSimilarity, minimalSimilarity, similarity, isSuspicious);
    }
}
