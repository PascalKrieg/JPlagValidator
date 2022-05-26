package ba.kripas.dataset;

import ba.kripas.JarConfig;
import ba.kripas.jplag.OptionsOverride;
import ba.kripas.running.EvaluationConfig;
import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DataLoader implements IDataLoader {
    private static final String PAIR_FILE_NAME = "truths.txt";
    private static final String CONFIG_FILE_NAME = "config.json";

    private final String datasetRootPath;
    private final String jarRootPath;

    public DataLoader(String datasetRootPath, String jarRootPath) {
        this.datasetRootPath = datasetRootPath;
        this.jarRootPath = jarRootPath;
    }

    @Override
    public EvaluationConfig LoadConfig() {

    }

    private List<JarConfig> loadJarConfigs() throws IOException {
        var content = new String(Files.readAllBytes(Paths.get(jarRootPath, CONFIG_FILE_NAME)));
        var fullFile = new JSONObject(content);
        var jarsArray = fullFile.getJSONArray("jars");

    }

    private List<JarConfig> buildJarConfig(JSONObject jarEntry) {
        var jarFileName = jarEntry.getString("file");
        var commitId = jarEntry.getString("commit");
        var configs = jarEntry.getJSONArray("configs");
    }

    /*
    {
        "setter" : "setSpecificParameter",
        "type" : "int",
        "value" : 8
    }
     */
    private OptionsOverride buildOptionsOverride(JSONObject overrideEntry) {
        var setterName = overrideEntry.getString("setter");
        var type = overrideEntry.getString("type");
        var value = overrideEntry.getString("value");
        return new OptionsOverride(setterName,type, value);
    }


}
