package codesquad.application.handler;

import codesquad.application.db.InMemoryUserRepository;
import codesquad.application.model.User;
import codesquad.was.http.HttpHeader;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.Handler;
import java.util.List;

public class UserListHandler extends Handler {

    private InMemoryUserRepository userRepository;

    public UserListHandler(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.authenticate();
        if (!request.isAuthenticated()) {
            response.sendRedirect("/login/index.html");
        } else {
            HttpHeaders headers = HttpHeaders.getDefault();
            headers.setHeader(new HttpHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.html.getMIMEType()));
            response.send(
                    headers,
                    createHtml(request.getPrincipal().getUsername(), userRepository.findAll()).getBytes());
        }
    }

    private String createHtml(String userName, List<User> users) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\"/>");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
        html.append("    <link href=\"../reset.css\" rel=\"stylesheet\"/>");
        html.append("    <link href=\"../global.css\" rel=\"stylesheet\"/>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("    <header class=\"header\">");
        html.append("        <a href=\"/\"><img src=\"../img/signiture.svg\"/></a>");
        html.append("        <ul class=\"header__menu\">");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <a class=\"btn btn_contained btn_size_s\" href=\"/index.html\">");
        html.append(userName);
        html.append("                </a>");
        html.append("            </li>");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <a class=\"btn btn_contained btn_size_s\" href=\"/index.html\">");
        html.append("홈");
        html.append("                </a>");
        html.append("            </li>");
        html.append("        </ul>");
        html.append("    </header>");
        html.append("    <div class=\"page\">");
        html.append("        <h2 class=\"page-title\">User List</h2>");
        html.append("        <table class=\"user-table\" border=\"1\">");
        html.append("            <thead>");
        html.append("            <tr>");
        html.append("                <th>Username</th>");
        html.append("                <th>Nickname</th>");
        html.append("            </tr>");
        html.append("            </thead>");
        html.append("            <tbody>");

        // 사용자 데이터를 테이블에 추가
        for (User user : users) {
            html.append("            <tr>");
            html.append("                <td> ").append(user.getUsername()).append("</td>");
            html.append("                <td> ").append(user.getNickname()).append("</td>");
            html.append("            </tr");
        }

        html.append("            </tbody>");
        html.append("        </table>");
        html.append("    </div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        html.append("    </div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
