package ba.kripas;

import ba.kripas.dataset.DataLoader;
import ba.kripas.reporting.IResultWriter;
import ba.kripas.reporting.MultiCSVSummaryWriter;
import ba.kripas.running.JPlagRunner;

import java.io.IOException;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger("ba.kripas.App");

    public static void main(String[] args) {
        String baseDirectory = args[0];

        String DATASET_PATH = baseDirectory + "/dataset";
        String JARS_PATH = baseDirectory + "/jars";
        String RESULT_PATH = baseDirectory + "/results";

        try {
            logger.fine("Initialize loader");
            var loader = new DataLoader(DATASET_PATH, JARS_PATH);

            logger.fine("Loading config");
            var config = loader.LoadConfig();
            logger.fine("Config loaded");

            logger.fine("Initializing runner");
            JPlagRunner runner = new JPlagRunner(config.getJarConfigs(), config.getProjects());

            logger.fine("Running...");
            var summary = runner.Run();
            logger.fine("Finished Running");

            logger.fine("Writing result");
            IResultWriter writer = new MultiCSVSummaryWriter(RESULT_PATH);

            writer.writeResult(summary);
            logger.fine("Finished running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}