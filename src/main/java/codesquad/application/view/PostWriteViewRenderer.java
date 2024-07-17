package codesquad.application.view;

import java.util.Map;

public class PostWriteViewRenderer extends ViewRenderer {
    @Override
    protected void renderInternal(StringBuilder html, Map<String, Object> parameters) {
        String loginUsername = (String) parameters.get("loginUsername");
        Long userId = (Long) parameters.get("userId");

        html.append("<div class=\"wrapper\">");
        createHeaderWithAuthenticatedUser(html, loginUsername);
        html.append("    <div class=\"page\">");
        html.append("       <h2 class=\"page-title\">게시글 작성</h2>");
        html.append("       <form class=\"form\" action = \"/post\" method = \"post\">");
        html.append(
                "                     <input type = \"hidden\" name = \"userId\" value = \"");
        html.append(userId);
        html.append("\"/>");
        html.append("           <div class=\"textfield textfield_size_s\" style = \"width: 680px\">");
        html.append("                 <p class=\"title_textfield\">제목</p>");
        html.append(
                "                     <input class = \"input_textfield\" name = \"title\"  placeholder = \"제목을 입력해주세요\"/>");

        html.append("           </div>");
        html.append("           <div class=\"textfield textfield_size_m\">");
        html.append(
                "               <textarea class=\"input_textfield\" name = \"content\" placeholder=\"글의 내용을 입력하세요\"></textarea>");
        html.append("           </div>");
        html.append(
                "               <button type=\"submit\" class=\"btn btn_contained btn_size_m\" style=\"margin-top: 24px\">");
        html.append("           작성 완료");
        html.append("           </button>");
        html.append("       </form>");
        html.append("   </div>");
        html.append("</div");
    }
}
