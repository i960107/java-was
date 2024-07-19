package codesquad.application.view;

import java.util.Map;

public abstract class ViewRenderer {

    public String render(Map<String, Object> parameters) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\"/>\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n");
        html.append("    <link href=\"/reset.css\" rel=\"stylesheet\"/>\n");
        html.append("    <link href=\"/global.css\" rel=\"stylesheet\"/>\n");
        html.append("    <link href=\"/main.css\" rel=\"stylesheet\"/>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"container\">\n");
        renderInternal(html, parameters);
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }

    protected abstract void renderInternal(StringBuilder html, Map<String, Object> parameters);

    protected void createHeaderWithAuthenticatedUser(StringBuilder html, String username) {
        html.append("    <header class=\"header\">\n");
        html.append("        <a href=\"/\"><img src=\"/img/signiture.svg\"/></a>\n");
        html.append("        <ul class=\"header__menu\">\n");
        html.append("            <li class=\"header__menu__item\">\n");
        html.append("                <button class=\"btn btn_contained btn_size_s\">");
        html.append(username);
        html.append("                </button>\n");
        html.append("            </li>\n");
        html.append("            <li class=\"header__menu__item\">\n");
        html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/logout\">");
        html.append("로그 아웃");
        html.append("                </a>\n");
        html.append("            </li>\n");
        html.append("        </ul>\n");
        html.append("    </header>\n");
    }

    protected void createHeaderWithAnonymousUser(StringBuilder html) {
        html.append("    <header class=\"header\">\n");
        html.append("        <a href=\"/\"><img src=\"./img/signiture.svg\"/></a>\n");
        html.append("        <ul class=\"header__menu\">\n");
        html.append("            <li class=\"header__menu__item\">\n");
        html.append("                <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n");
        html.append("            </li>\n");
        html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/registration/index.html\">\n");
        html.append("회원 가입");
        html.append("                </a>\n");
        html.append("            </li>\n");
        html.append("        </ul>\n");
        html.append("    </header>\n");
    }

    protected void createButton(StringBuilder html, String method, String actionUrl, String title) {
        html.append("       <form class=\"form\" action = \"" + actionUrl + "\" method = \"" + method + "\">\n");
        html.append(
                "               <button type=\"submit\" class=\"btn btn_contained btn_size_m\" style=\"margin-top: 24px\">\n");
        html.append("           글쓰기");
        html.append("           </button>\n");
        html.append("       </form>\n");
    }

}
