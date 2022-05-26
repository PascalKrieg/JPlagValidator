package ba.kripas.dataset;

import ba.kripas.JarConfig;
import ba.kripas.jplag.OptionsOverride;
import ba.kripas.running.RunningConfig;
import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataLoader implements IDataLoader {
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String TRUTH_ENDING = "-truth.txt";

    private final String datasetRootPath;
    private final String jarRootPath;

    public DataLoader(String datasetRootPath, String jarRootPath) {
        this.datasetRootPath = datasetRootPath;
        this.jarRootPath = jarRootPath;
    }

    @Override
    public RunningConfig LoadConfig() throws IOException {
        var jarConfigs = loadJarConfigs();
        var projects = loadProjects();

        return new RunningConfig(projects, jarConfigs);
    }

    private List<Project> loadProjects() throws IOException {
        var rootDirectoryFile = new File(datasetRootPath);

        var projectList = new LinkedList<Project>();

        var subDirectories = rootDirectoryFile.listFiles(File::isDirectory);
        var truthFiles = rootDirectoryFile.listFiles(file -> {
            if (file.isDirectory())
                return false;
            return file.getName().endsWith(TRUTH_ENDING);
        });

        assert subDirectories != null;
        for (var subDir : subDirectories) {
            var name = subDir.getName();
            // TODO detect automatically
            var language = Language.C_CPP;

            assert truthFiles != null;
            for (var truthFile : truthFiles) {
                if (truthFile.getName().equals(name + TRUTH_ENDING)) {
                    var project = new Project(name, subDir.toPath(), language, parseTruthFile(truthFile));
                    projectList.add(project);
                    break;
                }
            }
        }
        return projectList;
    }

    private List<SubmissionPair> parseTruthFile(File truthFile) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(truthFile));

        var currentPairs = new LinkedList<SubmissionPair>();
        var currentType = SubmissionPairType.NO_PLAGIARISM;

        String line = reader.readLine();

        while (line != null) {
            if (line.startsWith(">")) {
                currentType = parseTypeLine(line);
                line = reader.readLine();
                continue;
            }
            currentPairs.addAll(parsePairLine(line, currentType));

            line = reader.readLine();
        }

        reader.close();
        return currentPairs;
    }

    private SubmissionPairType parseTypeLine(String fullLine) {
        var line = fullLine.toLowerCase();
        line = line.replaceAll(" ", "");
        return switch (line) {
            case ">common_plag" -> SubmissionPairType.COMMON_PLAG;
            case ">mossad_plag" -> SubmissionPairType.MOSSAD_PLAG;
            case ">mossad_sibling" -> SubmissionPairType.MOSSAD_SIBLING;
            case ">hybrid_plag" -> SubmissionPairType.HYBRID_PLAG;
            case ">hybrid_sibling" -> SubmissionPairType.HYBRID_SIBLING;
            default -> SubmissionPairType.NO_PLAGIARISM;
        };
    }

    private List<SubmissionPair> parsePairLine(String fullLine, SubmissionPairType currentType) {
        var entries = fullLine.split(",");
        List<SubmissionPair> pairs = new LinkedList<>();
        for (int i = 0; i < entries.length; i++) {
            for (int j = i + 1; j < entries.length; j++) {
                var first = entries[i].trim();
                var second = entries[j].trim();
                pairs.add(new SubmissionPair(first, second, currentType));
            }
        }
        return pairs;
    }

    private List<JarConfig> loadJarConfigs() throws IOException {
        var content = new String(Files.readAllBytes(Paths.get(jarRootPath, CONFIG_FILE_NAME)));
        var fullFile = new JSONObject(content);
        var jarsArray = fullFile.getJSONArray("jars");

        var configs = new ArrayList<JarConfig>();

        jarsArray.forEach(element -> {
            configs.add(buildJarConfig((JSONObject) element));
        });

        return configs;
    }

    private JarConfig buildJarConfig(JSONObject jarEntry) {
        var jarFileName = jarEntry.getString("file");
        var commitId = jarEntry.getString("commit");
        var configs = jarEntry.getJSONArray("configs");
        // TODO: Parse configs
        return new JarConfig(new File(jarRootPath, jarFileName), commitId, new LinkedList<>());
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
        return new OptionsOverride(setterName, type, value);
    }


}
