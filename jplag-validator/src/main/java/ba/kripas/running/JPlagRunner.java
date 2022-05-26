package ba.kripas.running;

import ba.kripas.JarConfig;
import ba.kripas.Summary;
import ba.kripas.dataset.Project;
import ba.kripas.jplag.IncompatibleInterface;
import ba.kripas.jplag.JPlagWrapper;

import java.net.MalformedURLException;
import java.util.*;

public class JPlagRunner {
    private final List<JarConfig> jarConfigs;
    private final List<Project> projects;

    public JPlagRunner(List<JarConfig> jarConfigs, List<Project> projects) {
        this.jarConfigs = jarConfigs;
        this.projects = projects;
    }

    public Summary Run() {
        var result = new ArrayList<JarRunResult>();

        for (var config : jarConfigs) {
            try {
                result.add(RunJar(config, this.projects));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IncompatibleInterface e) {
                e.printStackTrace();
            }
        }

        var aggregate = new Summary(result);

        return aggregate;
    }

    public static JarRunResult RunJar(JarConfig jarConfig, List<Project> projects) throws MalformedURLException, IncompatibleInterface {
        var jPlag = new JPlagWrapper(jarConfig.getJarFile());
        jPlag.Load();

        List<ProjectRunResult> results = new ArrayList<>();
        List<Project> errorProjects = new ArrayList<>();

        for (var project : projects) {
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
