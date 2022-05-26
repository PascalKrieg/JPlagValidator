package ba.kripas;

import ba.kripas.running.JarRunResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Summary {
    private final List<JarRunResult> jarResults;
    private final Date timestamp;

    public List<JarRunResult> getJarResults() {
        return jarResults;
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
