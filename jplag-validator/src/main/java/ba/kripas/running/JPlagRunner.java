package ba.kripas.running;

import ba.kripas.dataset.Project;
import ba.kripas.jplag.IncompatibleInterfaceException;
import ba.kripas.jplag.InvalidOptionsException;
import ba.kripas.jplag.JPlagWrapper;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JPlagRunner {
    private static Logger logger = Logger.getLogger("ba.kripas.running.JPlagRunner");

    private final List<JarConfig> jarConfigs;
    private final List<Project> projects;

    public JPlagRunner(List<JarConfig> jarConfigs, List<Project> projects) {
        this.jarConfigs = jarConfigs;
        this.projects = projects;
    }

    public Summary run() {
        var result = new ArrayList<JarRunResult>();

        for (var config : jarConfigs) {
            try {
                result.add(runJar(config, this.projects));
            } catch (MalformedURLException | IncompatibleInterfaceException | InvalidOptionsException e) {
                e.printStackTrace();
            }
        }

        return new Summary(result);
    }

    public static JarRunResult runJar(JarConfig jarConfig, List<Project> projects) throws MalformedURLException,
            IncompatibleInterfaceException, InvalidOptionsException {
        logger.info("Now Running Jar: " + jarConfig.getJarFile().getName() + " - " + jarConfig.getConfigId());

        var jPlag = new JPlagWrapper(jarConfig);

        List<ProjectRunResult> results = new ArrayList<>();
        List<Project> errorProjects = new ArrayList<>();

        for (var project : projects) {
            logger.info("Project: " + project.getName());
            try {
                var resultWrapper = jPlag.run(project);
                var runResult = new ProjectRunResult(jarConfig, project, resultWrapper);
                results.add(runResult);
            } catch (Exception e) {
                e.printStackTrace();
                errorProjects.add(project);
            }
        }

        return new JarRunResult(jarConfig, results, errorProjects);
    }
}
