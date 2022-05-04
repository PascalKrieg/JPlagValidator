package ba.kripas;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.exceptions.ExitException;

import java.util.*;

public class EvaluationRunner {

    private final IResultWriter resultWriter;
    private final EvaluationOptions options;

    private final Map<String, Float> submissionSimilarity = new HashMap<>();

    public EvaluationRunner(IResultWriter resultWriter, EvaluationOptions options) {
        this.resultWriter = resultWriter;
        this.options = options;
    }

    public void Run() {
        try {
            List<JPlagComparison> comparisons = runJPlag();
            fillSubmissionSimilarity(comparisons);
            resultWriter.WriteResults(buildEvaluationsList());
        } catch (ExitException e) {
            e.printStackTrace();
        }
    }

    private List<JPlagComparison> runJPlag() throws ExitException {
        JPlag jplag = new JPlag(options.getJPlagOptions());
        JPlagResult result = jplag.run();
        return result.getComparisons();
    }

    private void fillSubmissionSimilarity(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission firstSubmission = comparison.getFirstSubmission();
            Submission secondSubmission = comparison.getSecondSubmission();
            setSimilarityIfMax(firstSubmission, comparison.maximalSimilarity());
            setSimilarityIfMax(secondSubmission, comparison.maximalSimilarity());
        }
    }

    private void setSimilarityIfMax(Submission submission, float similarity) {
        float currentSimilarity = submissionSimilarity.getOrDefault(submission.getName(), 0f);
        if (currentSimilarity >= similarity)
            return;
        System.out.println("Replacing similarity " + currentSimilarity + " with similarity " + similarity + " for submission " + submission.getName() );
        submissionSimilarity.put(submission.getName(), similarity);
    }

    private List<SubmissionEvaluation> buildEvaluationsList() {
        List<SubmissionEvaluation> evaluationList = new ArrayList<>();
        for (String submission : submissionSimilarity.keySet()) {
            float similarity = submissionSimilarity.getOrDefault(submission, 0f);
            boolean isSuspicious = (similarity > options.getSimilarityThreshold());


            SubmissionEvaluation evaluation = new SubmissionEvaluation(submission,
                    submissionSimilarity.getOrDefault(submission, 0f),
                    isSuspicious,
                    SubmissionType.REAL);
            evaluationList.add(evaluation);
        }
        return evaluationList;
    }

}
