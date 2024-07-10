package codesquad.server;

import codesquad.http.WasRequest;
import codesquad.server.exception.NoMatchHandlerException;
import java.util.LinkedList;
import java.util.List;

public class HandlerContext {

    private static HandlerContext INSTANCE;

    private List<Handler> handlers;

    private HandlerContext() {
        handlers = new LinkedList<>();
    }

    public static HandlerContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HandlerContext();
        }
        return INSTANCE;
    }

    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }


    public Handler getMappedHandler(WasRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(NoMatchHandlerException::new);
    }
}
