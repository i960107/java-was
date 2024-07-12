package codesquad.was.http;

import java.io.IOException;

public interface Endpoint<S, U> {

    void bind() throws IOException;

    void startInternal() throws IOException;

    void stopInternal() throws IOException;

    void setRunning(boolean isRunning);

    default void start() throws IOException {
        bind();
        setRunning(true);
        startInternal();
    }

    default void stop() throws IOException {
        setRunning(false);
        stopInternal();
    }

}
