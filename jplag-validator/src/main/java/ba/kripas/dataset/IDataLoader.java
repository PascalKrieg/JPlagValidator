package ba.kripas.dataset;

import ba.kripas.running.RunningConfig;

import java.io.IOException;

public interface IDataLoader {

    RunningConfig loadConfig() throws IOException;

}
