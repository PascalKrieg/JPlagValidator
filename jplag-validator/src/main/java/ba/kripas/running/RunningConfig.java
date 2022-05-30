package ba.kripas.running;

import ba.kripas.dataset.Project;


import java.util.List;

public class RunningConfig {
    private final List<Project> projects;
    private final List<JarConfig> jarConfigs;

    public RunningConfig(List<Project> projects, List<JarConfig> jarConfigs) {
        this.projects = projects;
        this.jarConfigs = jarConfigs;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<JarConfig> getJarConfigs() {
        return jarConfigs;
    }
}
