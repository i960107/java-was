package codesquad.was.http;

import codesquad.was.http.JIoEndpoint.SocketProcessor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JIoEndpoint implements Endpoint<Socket, SocketProcessor> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private final ExecutorService acceptorExecutorService;

    private final ExecutorService workerExecutorService;

    private final ArrayBlockingQueue<HttpProcessor> processors;

    private final Supplier<HttpProcessor> processorSupplier;

    private final int port;

    private final int backlog;

    private boolean running = false;

    public JIoEndpoint(int port,
                       int backlog,
                       ExecutorService acceptorExecutorService,
                       ExecutorService workerExecutorService,
                       Supplier<HttpProcessor> processorSupplier) {
        this.port = port;
        this.backlog = backlog;
        this.acceptorExecutorService = acceptorExecutorService;
        this.workerExecutorService = workerExecutorService;
        int workers = ((ThreadPoolExecutor) workerExecutorService).getCorePoolSize();
        log.info("acceptor thread created");
        log.info("{} worker threads created", workers);

        this.processors = new ArrayBlockingQueue<>(workers);
        this.processorSupplier = processorSupplier;
    }

    @Override
    public void bind() throws IOException {
        serverSocket = new ServerSocket(port, backlog);
        log.info("Listening on port {}", port);
    }

    @Override
    public void startInternal() throws IOException {
        acceptorExecutorService.execute(new Acceptor());
    }


    @Override
    public void stopInternal() throws IOException {
        serverSocket.close();
        if (workerExecutorService != null) {
            workerExecutorService.shutdown();
        }
        if (acceptorExecutorService != null) {
            acceptorExecutorService.shutdown();
        }
        log.info("exit: executor services are shutdown and server socket is closed");
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    public SocketProcessor createWorker(Socket socket) {
        return new SocketProcessor(socket);
    }

    private HttpProcessor getProcessor() {
        HttpProcessor processor = processors.poll();
        if (processor == null) {
            processor = processorSupplier.get();
        }
        return processor;
    }

    private void recycleProcessor(HttpProcessor processor) {
        processors.offer(processor);
    }

    public class Acceptor implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketProcessor worker = createWorker(socket);
                    log.info("acceptor accepts connection and delegating it to worker thread");
                    workerExecutorService.execute(worker);
                } catch (IOException exception) {
                    log.warn("fail while accepting socket and allocating it to worker thread");
                }
            }
        }
    }

    public class SocketProcessor implements Runnable {
        private final Socket socket;

        public SocketProcessor(
                Socket socket
        ) {
            this.socket = socket;
        }

        @Override
        public void run() {
            HttpProcessor processor = getProcessor();
            try {
                processor.process(socket);
            } finally {
                recycleProcessor(processor);
                try {
                    socket.close();
                } catch (IOException e) {
                    log.warn("exception closing socket", e);
                }
            }
        }
    }

}
