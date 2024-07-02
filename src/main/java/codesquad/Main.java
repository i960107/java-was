package codesquad;

import codesquad.http.HttpProtocol;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        HttpProtocol httpProtocol = new HttpProtocol();
        httpProtocol.start();
    }
}
