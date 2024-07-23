package codesquad.application.view;

import codesquad.application.model.User;
import java.util.List;
import java.util.Map;

public class UserListViewRenderer extends ViewRenderer {
    @Override
    protected void renderInternal(StringBuilder html, Map<String, Object> parameters) {
        String loginUsername = (String) parameters.get("loginUsername");
        List<User> users = (List<User>) parameters.get("users");

        html.append("    <div class=\"wrapper\">");

        createHeaderWithAuthenticatedUser(html, loginUsername);

        html.append("     <div class=\"page\">");
        html.append("        <h2 class=\"page-title\" >👯 User List 👯 </h2>");
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
        html.append("    </div>");
    }
}
