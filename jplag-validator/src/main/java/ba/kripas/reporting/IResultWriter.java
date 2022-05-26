package ba.kripas.reporting;

import ba.kripas.ResultAggregate;

public interface IResultWriter {
    void writeResult(ResultAggregate result);
}
