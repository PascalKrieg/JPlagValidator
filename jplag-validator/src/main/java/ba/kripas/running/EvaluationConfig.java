package ba.kripas.running;

import ba.kripas.JarConfig;
import ba.kripas.dataset.Project;


import java.util.List;

public class EvaluationConfig {
    private final List<Project> projects;
    private final List<JarConfig> jarConfigs;
    private final EvaluationOptions options;

    public EvaluationConfig(List<Project> projects, List<JarConfig> jarConfigs, EvaluationOptions options) {
        this.projects = projects;
        this.jarConfigs = jarConfigs;
        this.options = options;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<JarConfig> getJarConfigs() {
        return jarConfigs;
    }

    public EvaluationOptions getOptions() {
        return options;
    }
}
