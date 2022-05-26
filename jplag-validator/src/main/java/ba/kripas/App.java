package ba.kripas;

import ba.kripas.dataset.DataLoader;
import ba.kripas.reporting.IResultWriter;
import ba.kripas.reporting.MultiCSVSummaryWriter;
import ba.kripas.running.JPlagRunner;

import java.io.IOException;

public class App
{
    public static void main( String[] args )
    {
        String baseDirectory = args[0];

        String DATASET_PATH = baseDirectory + "/dataset";
        String JARS_PATH = baseDirectory + "/jars";
        String RESULT_PATH = baseDirectory + "/results";

        try {
            System.out.println("Initialize loader");
            var loader = new DataLoader(DATASET_PATH, JARS_PATH);

            System.out.println("Loading config");
            var config = loader.LoadConfig();
            System.out.println("Config loaded");

            System.out.println("Initializing runner");
            JPlagRunner runner = new JPlagRunner(config.getJarConfigs(), config.getProjects());

            System.out.println("Running...");
            var summary = runner.Run();
            System.out.println("Finished Running");

            System.out.println("Writing result");
            IResultWriter writer = new MultiCSVSummaryWriter(RESULT_PATH);

            writer.writeResult(summary);
            System.out.println("Finished running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}