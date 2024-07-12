package codesquad.was.http;

import codesquad.was.server.ServerContext;
import codesquad.was.http.JIoEndpoint.SocketProcessor;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

public class HttpProtocol {
    private static final String namePrefix = "http-bio";

    private final Endpoint<Socket, SocketProcessor> endpoint;

    public HttpProtocol(ServerContext handlerServerContext) {
        NamedThreadFactory acceptorThreadFactory = new NamedThreadFactory("http-bio-acceptor");
        NamedThreadFactory workerThreadFactory = new NamedThreadFactory(namePrefix);

        int workerThreadCount = calculateOptimalThreads();
        this.endpoint = new JIoEndpoint(
                8080,
                workerThreadCount * 2,
                Executors.newSingleThreadExecutor(acceptorThreadFactory),
                Executors.newFixedThreadPool(calculateOptimalThreads(), workerThreadFactory),
                () -> new HttpProcessor(handlerServerContext)
        );
    }

    private int calculateOptimalThreads() {
        int availableProcessor = Runtime.getRuntime().availableProcessors();
        return availableProcessor * 10;
    }

    public void start() throws IOException {
        endpoint.start();
    }

    public void stop() throws IOException {
        endpoint.stop();
    }

}