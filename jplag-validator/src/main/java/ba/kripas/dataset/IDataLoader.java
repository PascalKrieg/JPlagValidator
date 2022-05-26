package ba.kripas.dataset;

import ba.kripas.running.RunningConfig;

import java.io.IOException;

public interface IDataLoader {

    RunningConfig LoadConfig() throws IOException;

}
