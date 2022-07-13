package ba.kripas.running;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Summary {
    private final List<JarRunResult> jarResults;
    private final Date timestamp;

    public Collection<JarRunResult> getJarResults() {
        return jarResults;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Summary() {
        this.timestamp = new Date();
        this.jarResults = new ArrayList<>();
    }

    public Summary(List<JarRunResult> jarResults) {
        this.timestamp = new Date();
        this.jarResults = jarResults;
    }

    public void AddRunResult(JarRunResult runResult) {
        jarResults.add(runResult);
    }

}
