package codesquad.application.view;

import java.util.Map;

public abstract class ViewRenderer {

    public String render(Map<String, Object> parameters) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\"/>");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
        html.append("    <link href=\"/reset.css\" rel=\"stylesheet\"/>");
        html.append("    <link href=\"/global.css\" rel=\"stylesheet\"/>");
        html.append("    <link href=\"/main.css\" rel=\"stylesheet\"/>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        renderInternal(html, parameters);
        html.append("</div>");
        html.append("</body>");
        return html.toString();
    }

    protected abstract void renderInternal(StringBuilder html, Map<String, Object> parameters);

    protected void createHeaderWithAuthenticatedUser(StringBuilder html, String username) {
        html.append("    <header class=\"header\">");
        html.append("        <a href=\"/\"><img src=\"/img/signiture.svg\"/></a>");
        html.append("        <ul class=\"header__menu\">");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <button class=\"btn btn_contained btn_size_s\">");
        html.append(username);
        html.append("                </button>");
        html.append("            </li>");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/logout\">");
        html.append("로그 아웃");
        html.append("                </a>");
        html.append("            </li>");
        html.append("        </ul>");
        html.append("    </header>");
    }

    protected void createHeaderWithAnonymousUser(StringBuilder html) {
        html.append("    <header class=\"header\">");
        html.append("        <a href=\"/\"><img src=\"./img/signiture.svg\"/></a>");
        html.append("        <ul class=\"header__menu\">");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>");
        html.append("            </li>");
        html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/registration/index.html\">");
        html.append("회원 가입");
        html.append("                </a>");
        html.append("            </li>");
        html.append("        </ul>");
        html.append("    </header>");
    }

    protected void createButton(StringBuilder html, String method, String actionUrl, String title) {
        html.append("       <form class=\"form\" action = \"" + actionUrl + "\" method = \"" + method + "\">");
        html.append(
                "               <button type=\"submit\" class=\"btn btn_contained btn_size_m\" style=\"margin-top: 24px\">");
        html.append("           글쓰기");
        html.append("           </button>");
        html.append("       </form>");
    }

}
