package ba.kripas;

import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class App
{
    public static void main( String[] args )
    {
        JPlagOptions options = new JPlagOptions("/Users/pascal/Bachelorarbeit/AutomaticEval/dataset", LanguageOption.C_CPP);
        EvaluationOptions evaluationOptions = new EvaluationOptions(options, 20f);

        IResultWriter writer = new CSVResultWriter("/Users/pascal/Bachelorarbeit/AutomaticEval");

        EvaluationRunner evaluationRunner = new EvaluationRunner(writer, evaluationOptions);

        evaluationRunner.Run();
    }
}