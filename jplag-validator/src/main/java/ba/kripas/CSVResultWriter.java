package ba.kripas;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;

public class CSVResultWriter implements IResultWriter {

    private final String targetPath;


    public CSVResultWriter(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void WriteResults(Collection<SubmissionEvaluation> evaluations) {
        try {
            Path filePath = FileSystems.getDefault().getPath(targetPath, buildFileName());
            FileWriter writer = new FileWriter(filePath.toFile());
            String content = buildFileContent(evaluations);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildFileName() {
        return "evaluation.csv";
        //return (new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date())) + ".csv";
    }

    private String buildFileContent(Collection<SubmissionEvaluation> evaluations) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(header);
        for (SubmissionEvaluation evaluation : evaluations) {
            stringBuilder.append(buildLine(evaluation));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private final String header = "\"submission\",\"type\",\"max_similarity\",\"suspicious\",\"false_positive\",\"false_negative\"\n";
    private String buildLine(SubmissionEvaluation submissionEvaluation) {
        return String.format( "\"%s\",\"%s\",\"%f\",\"%d\",\"%d\",\"%d\"",
                submissionEvaluation.getSubmissionName(),
                submissionEvaluation.getActualType().toString(),
                submissionEvaluation.getMaxSimilarity(),
                submissionEvaluation.isSuspicious() ? 1 : 0,
                submissionEvaluation.isFalsePositive() ? 1 : 0,
                submissionEvaluation.isFalseNegative() ? 1 : 0);

    }
}
