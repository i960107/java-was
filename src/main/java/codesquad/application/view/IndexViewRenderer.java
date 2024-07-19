package codesquad.application.view;

import codesquad.application.handler.exception.ViewRenderException;
import codesquad.application.model.PostDetailsDto;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexViewRenderer extends ViewRenderer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void renderInternal(StringBuilder html, Map<String, Object> parameters) {
        try {
            html.append("    <div class=\"wrapper\">");

            String loginUsername = (String) parameters.get("loginUsername");
            if (loginUsername != null) {
                createHeaderWithAuthenticatedUser(html, loginUsername);
            } else {
                createHeaderWithAnonymousUser(html);
            }
            List<PostDetailsDto> posts = (List<PostDetailsDto>) parameters.get("posts");
            for (PostDetailsDto post : posts) {
                //todo account  img, post img
                html.append("        <div class=\"post\">");
                html.append("            <div class=\"post__account\">");
                html.append("                <img class=\"post__account__img\"/>");
                html.append(
                        "                <p class=\"post__account__nickname\">" + post.getAuthorNickname() + "</p>");
                html.append("            </div>");
                html.append("            <img class=\"post__img\" src=\"upload/");
                html.append(post.getFileName());
                html.append("\"/>");
                html.append("            <input type=\"hidden\" name = \"postId\" value=\"");
                html.append(post.getPostId());
                html.append("\"/>");
                html.append("            <div class=\"post__menu\">");
                html.append("                <ul class=\"post__menu__personal\">");
                html.append("                    <li>");
                html.append("                        <button class=\"post__menu__btn\">");
                html.append("                            <img src=\"/img/like.svg\"/>");
                html.append("                        </button>");
                html.append("                    </li>");
                html.append("                    <li>");
                html.append("                        <button class=\"post__menu__btn\">");
                html.append("                            <img src=\"/img/sendLink.svg\"/>");
                html.append("                        </button>");
                html.append("                    </li>");
                html.append("                </ul>");
                html.append("                <button class=\"post__menu__btn\">");
                html.append("                    <img src=\"/img/bookMark.svg\"/>");
                html.append("                </button>");
                html.append("            </div>");
                html.append("            <p class=\"post__title\">");
                html.append(post.getTitle());
                html.append("            </p>");
                html.append("            <p class=\"post__article\">");
                html.append(post.getContent());
                html.append("            </p>");
                html.append("        </div>");
            }

            createButton(html, "get", "/post", "글쓰기");
            html.append("        </div>");
        } catch (ClassCastException exception) {
            log.warn("class cast exception occurred while rendering index view {} ", exception.getMessage());
            throw new ViewRenderException();
        }
    }
}
