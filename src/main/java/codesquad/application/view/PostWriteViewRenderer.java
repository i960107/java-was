package codesquad.application.view;

import java.util.Map;

public class PostWriteViewRenderer extends ViewRenderer {
    @Override
    protected void renderInternal(StringBuilder html, Map<String, Object> parameters) {
        String loginUsername = (String) parameters.get("loginUsername");
        Long userId = (Long) parameters.get("userId");
        html.append("<div class=\"wrapper\">\n");
        createHeaderWithAuthenticatedUser(html, loginUsername);
        html.append("    <div class=\"page\">\n");
        html.append("       <h2 class=\"page-title\">게시글 작성</h2>\n");
        html.append(
                "       <form class=\"form\" action = \"/post\" method = \"post\"  enctype=\"multipart/form-data\">\n");
        html.append("           <input type = \"hidden\" name = \"userId\" value = \"");
        html.append(userId);
        html.append("\"/>\n");
        html.append("           <div class=\"textfield textfield_size_s\" style = \"width: 680px\">\n");
        html.append("                 <p class=\"title_textfield\">제목</p>\n");
        html.append(
                "                 <input  class = \"input_textfield\" name = \"title\"  required placeholder = \"제목을 입력해주세요\"/>\n");
        html.append("           </div>\n");
        html.append("           <div class=\"textfield textfield_size_s\" style = \"width: 680px\">\n");
        html.append("                 <p class=\"title_textfield\">이미지</p>\n");
        html.append(
                "                 <input  type = \"file\" class = \"input_textfield\" name = \"image\"  accept = \"image/*\" required placeholder = \"이미지 파일을 업로드해 주세요\"/>\n");
        html.append("           </div>\n");
        html.append("           <div class=\"textfield textfield_size_m\">\n");
        html.append(
                "               <textarea class=\"input_textfield\" name = \"content\" required placeholder=\"글의 내용을 입력하세요\"></textarea>\n");
        html.append("           </div>\n");
        html.append(
                "               <button type=\"submit\" class=\"btn btn_contained btn_size_m\" style=\"margin-top: 24px\">\n");
        html.append("           작성 완료\n");
        html.append("           </button>\n");
        html.append("       </form>\n");
        html.append("   </div>\n");
        html.append("</div>\n");
    }
}
