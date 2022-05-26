package ba.kripas;

import ba.kripas.jplag.IncompatibleInterface;
import ba.kripas.jplag.JPlagWrapper;

import java.io.File;
import java.net.MalformedURLException;

public class App
{
    public static void main( String[] args )
    {
        try {
            RunEvaluation("/Users/pascal/Bachelorarbeit/Code/JPlagValidator/jplag-test.jar");
            RunEvaluation("/Users/pascal/Bachelorarbeit/Code/JPlagValidator/jplag-test-2.jar");
        } catch (MalformedURLException e) {
            System.out.println("Jar does not exist.");
            e.printStackTrace();
        } catch (IncompatibleInterface e) {
            System.out.println("JPlag version does not seem to be compatible.");
            e.printStackTrace();
        }
    }

    private static void RunEvaluation(String jarPath) throws MalformedURLException, IncompatibleInterface {
        String submissionFolder = "/Users/pascal/Bachelorarbeit/AutomaticEval/dataset";
        String language = "C_CPP";

        File testJar = new File(jarPath);

        JPlagWrapper loader = new JPlagWrapper(testJar);
        loader.Load();
        loader.run(submissionFolder);
        //var JPlagWrapper = loader.buildJPlag(submissionFolder, language);


        /*
        EvaluationOptions evaluationOptions = new EvaluationOptions(20f);
        IResultWriter writer = new CSVResultWriter("/Users/pascal/Bachelorarbeit/AutomaticEval");
        EvaluationRunner evaluationRunner = new EvaluationRunner(writer, evaluationOptions);

        evaluationRunner.Run(jPlag);
         */

    }
}