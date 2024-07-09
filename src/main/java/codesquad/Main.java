package codesquad;

import codesquad.db.UserRepository;
import codesquad.http.HttpProtocol;
import codesquad.processor.UserRequestProcessor;
import codesquad.server.DefaultHandler;
import codesquad.server.HandlerContext;
import codesquad.server.UrlMappingHandler;
import java.io.IOException;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws IOException {
        HandlerContext handlerContext = HandlerContext.getInstance();
        handlerContext.addHandler(new DefaultHandler());
        handlerContext.addHandler(new UrlMappingHandler(
                Map.of(
                        "/create", new UserRequestProcessor(UserRepository.getInstance())
                )));

        HttpProtocol httpProtocol = new HttpProtocol(handlerContext);
        httpProtocol.start();
        //todo 종료 graceful하게
    }
}
