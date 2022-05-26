package ba.kripas;

import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.jplag.JPlagResultWrapper;
import ba.kripas.jplag.JPlagWrapper;
import ba.kripas.reporting.IResultWriter;
import ba.kripas.running.EvaluationOptions;

import java.util.*;

public class EvaluationRunner {
    private final IResultWriter resultWriter;
    private final EvaluationOptions options;

    private final Map<String, Float> submissionSimilarity = new HashMap<>();

    public EvaluationRunner(IResultWriter resultWriter, EvaluationOptions options) {
        this.resultWriter = resultWriter;
        this.options = options;
    }

    public void Run(JPlagWrapper jPlag) {
        try {
            List<JPlagComparisonWrapper> comparisons = runJPlag(jPlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<JPlagComparisonWrapper> runJPlag(JPlagWrapper jPlag) {
        JPlagResultWrapper result = jPlag.run();
        return result.getComparisons();
    }
}
