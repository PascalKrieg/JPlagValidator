package ba.kripas;

import java.util.Collection;

public interface IResultWriter {
    void WriteResults(Collection<SubmissionEvaluation> evaluations);
}
