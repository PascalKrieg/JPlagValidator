package ba.kripas.running;

import ba.kripas.jplag.OptionsOverride;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class JarConfig {
    private final File jarFile;
    private final String commitId;
    private final String configId;

    private final List<OptionsOverride> optionsOverrides;

    public File getJarFile() {
        return jarFile;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getConfigId() {
        return configId;
    }

    public Collection<OptionsOverride> getOptionsOverrides() {
        return optionsOverrides;
    }

    public JarConfig(File jarFile, String commitId, String configId, List<OptionsOverride> optionsOverrides) {
        this.jarFile = jarFile;
        this.commitId = commitId;
        this.configId = configId;
        this.optionsOverrides = optionsOverrides;
    }
}
