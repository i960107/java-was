package codesquad.http;

import codesquad.http.JIoEndpoint.SocketProcessor;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpProtocol {
    private static final String namePrefix = "http-bio";

    private final Endpoint<Socket, SocketProcessor> endpoint;

    public HttpProtocol() {
        NamedThreadFactory acceptorThreadFactory = new NamedThreadFactory("http-bio-acceptor");
        NamedThreadFactory workerThreadFactory = new NamedThreadFactory(namePrefix);

        this.endpoint = new JIoEndpoint(
                8080,
                50,
                Executors.newSingleThreadExecutor(acceptorThreadFactory),
                Executors.newFixedThreadPool(calculateOptimalThreads(), workerThreadFactory));
    }

    private int calculateOptimalThreads() {
        int availableProcessor = Runtime.getRuntime().availableProcessors();
        return availableProcessor * 2;
    }

    public void start() throws IOException {
        endpoint.start();
    }

    public void stop() throws IOException {
        endpoint.stop();
    }

}
