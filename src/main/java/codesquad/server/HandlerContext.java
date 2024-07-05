package codesquad.server;

import codesquad.http.WasRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HandlerContext {

    private static HandlerContext INSTANCE;

    private Set<Handler> handlers;

    private HandlerContext() {
        handlers = new HashSet<>();
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


    public Optional<Handler> getMappedHandler(WasRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst();
    }
}
