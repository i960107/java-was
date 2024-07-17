package codesquad.application.handler;

import codesquad.application.db.PostDao;
import codesquad.application.model.Post;
import codesquad.application.util.RequestParamModelMapper;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostWriteHandler extends Handler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PostDao postDao;

    public PostWriteHandler(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.authenticate();
        if (request.isAuthenticated()) {
            byte[] bytes = createHtml(
                    request.getPrincipal().getUserId(),
                    request.getPrincipal().getUsername()).getBytes();
            HttpHeaders headers = new HttpHeaders();
            HttpHeaders.setCommonHeader(headers);
            headers.setHeader(HttpHeaders.CONTENT_LENGTH_HEADER, String.valueOf(bytes.length));
            headers.setHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.html.getMIMEType());
            response.send(headers, bytes);
        } else {
            response.sendRedirect("/login/index.html");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Post post = RequestParamModelMapper.map(request.getParameterMap(), Post.class);
        Post postSaved = postDao.save(post);
        log.info("post saved {} ", postSaved.toString());
        response.sendRedirect("/index.html");
    }

    private String createHtml(Long userId, String userName) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\"/>");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
        html.append("    <link href=\"./reset.css\" rel=\"stylesheet\"/>");
        html.append("    <link href=\"./global.css\" rel=\"stylesheet\"/>");
        html.append("    <link href=\"./main.css\" rel=\"stylesheet\"/>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("    <header class=\"header\">");
        html.append("        <a href=\"/\"><img src=\"./img/signiture.svg\"/></a>");
        html.append("        <ul class=\"header__menu\">");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <button class=\"btn btn_contained btn_size_s\">");
        html.append(userName);
        html.append("                </button>");
        html.append("            </li>");
        html.append("            <li class=\"header__menu__item\">");
        html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/logout\">");
        html.append("로그 아웃");
        html.append("                </a>");
        html.append("            </li>");
        html.append("        </ul>");
        html.append("    </header>");
        html.append("    <div class=\"page\">");
        html.append("       <h2 class=\"page-title\">게시글 작성</h2>");
        html.append("       <form class=\"form\" action = \"/post\" method = \"post\">");
        html.append("           <div class=\"textfield textfield_size_m\">");
        html.append("           <p class=\"title_textfield\">내용</p>");
        html.append("           <input type=\"hidden\" name = \"userId\" value = \"" + userId + "\"/>");
        html.append(
                "               <textarea class=\"input_textfield\" name = \"content\" placeholder=\"글의 내용을 입력하세요\"></textarea>");
        html.append("           </div>");
        html.append(
                "               <button type=\"submit\" class=\"btn btn_contained btn_size_m\" style=\"margin-top: 24px\">");
        html.append("           작성 완료");
        html.append("           </button>");
        html.append("       </form>");
        html.append("   </div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
