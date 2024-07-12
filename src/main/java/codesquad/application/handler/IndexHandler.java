package codesquad.application.handler;

import codesquad.was.http.HttpHeader;
import codesquad.was.http.HttpHeaders;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.MimeTypes;
import codesquad.was.server.Handler;

public class IndexHandler implements Handler {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        request.authenticate();
        HttpHeaders headers = HttpHeaders.getDefault();
        headers.setHeader(new HttpHeader(HttpHeaders.CONTENT_TYPE_HEADER, MimeTypes.html.getMIMEType()));
        String username = request.getPrincipal() != null ? request.getPrincipal().getUsername() : null;
        response.send(
                headers,
                createHtml(username).getBytes());
    }

    private String createHtml(String userName) {
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
        if (userName == null) {
            html.append("                <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>");
        } else {
            html.append("                <button class=\"btn btn_contained btn_size_s\">");
            html.append(userName);
            html.append("                </button>");
        }
        html.append("            </li>");
        html.append("            <li class=\"header__menu__item\">");
        if (userName == null) {
            html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/registration/index.html\">");
            html.append("회원 가입");
            html.append("                </a>");
        } else {
            html.append("                <a class=\"btn btn_ghost btn_size_s\" href=\"/logout\">");
            html.append("로그 아웃");
            html.append("                </a>");
        }
        html.append("            </li>");
        html.append("        </ul>");
        html.append("    </header>");
        html.append("    <div class=\"wrapper\">");
        html.append("        <div class=\"post\">");
        html.append("            <div class=\"post__account\">");
        html.append("                <img class=\"post__account__img\"/>");
        html.append("                <p class=\"post__account__nickname\">account</p>");
        html.append("            </div>");
        html.append("            <img class=\"post__img\"/>");
        html.append("            <div class=\"post__menu\">");
        html.append("                <ul class=\"post__menu__personal\">");
        html.append("                    <li>");
        html.append("                        <button class=\"post__menu__btn\">");
        html.append("                            <img src=\"./img/like.svg\"/>");
        html.append("                        </button>");
        html.append("                    </li>");
        html.append("                    <li>");
        html.append("                        <button class=\"post__menu__btn\">");
        html.append("                            <img src=\"./img/sendLink.svg\"/>");
        html.append("                        </button>");
        html.append("                    </li>");
        html.append("                </ul>");
        html.append("                <button class=\"post__menu__btn\">");
        html.append("                    <img src=\"./img/bookMark.svg\"/>");
        html.append("                </button>");
        html.append("            </div>");
        html.append("            <p class=\"post__article\">");
        html.append("                우리는 시스템 아키텍처에 대한 일관성 있는 접근이 필요하며, 필요한");
        html.append("                모든 측면은 이미 개별적으로 인식되고 있다고 생각합니다. 즉, 응답이");
        html.append("                잘 되고, 탄력적이며 유연하고 메시지 기반으로 동작하는 시스템 입니다.");
        html.append("                우리는 이것을 리액티브 시스템(Reactive Systems)라고 부릅니다.");
        html.append("                리액티브 시스템으로 구축된 시스템은 보다 유연하고, 느슨한 결합을");
        html.append("                갖고, 확장성 이 있습니다. 이로 인해 개발이 더 쉬워지고 변경 사항을");
        html.append("                적용하기 쉬워집니다. 이 시스템은 장애 에 대해 더 강한 내성을 지니며,");
        html.append("                비록 장애가 발생 하더라도, 재난이 일어나기 보다는 간결한 방식으로");
        html.append("                해결합니다. 리액티브 시스템은 높은 응답성을 가지며 사용자 에게");
        html.append("                효과적인 상호적 피드백을 제공합니다.");
        html.append("            </p>");
        html.append("        </div>");
        html.append("        <ul class=\"comment\">");
        html.append("            <li class=\"comment__item\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">");
        html.append("                    군인 또는 군무원이 아닌 국민은 대한민국의 영역안에서는 중대한");
        html.append("                    군사상 기밀·초병·초소·유독음식물공급·포로·군용물에 관한 죄중");
        html.append("                    법률이 정한 경우와 비상계엄이 선포된 경우를 제외하고는 군사법원의");
        html.append("                    재판을 받지 아니한다.");
        html.append("                </p>");
        html.append("            </li>");
        html.append("            <li class=\"comment__item\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">");
        html.append("                    대통령의 임기연장 또는 중임변경을 위한 헌법개정은 그 헌법개정 제안");
        html.append("                    당시의 대통령에 대하여는 효력이 없다. 민주평화통일자문회의의");
        html.append("                    조직·직무범위 기타 필요한 사항은 법률로 정한다.");
        html.append("                </p>");
        html.append("            </li>");
        html.append("            <li class=\"comment__item\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">");
        html.append("                    민주평화통일자문회의의 조직·직무범위 기타 필요한 사항은 법률로");
        html.append("                    정한다.");
        html.append("                </p>");
        html.append("            </li>");
        html.append("            <li class=\"comment__item hidden\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">Comment 1</p>");
        html.append("            </li>");
        html.append("            <li class=\"comment__item hidden\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">Comment 2</p>");
        html.append("            </li>");
        html.append("            <li class=\"comment__item hidden\">");
        html.append("                <div class=\"comment__item__user\">");
        html.append("                    <img class=\"comment__item__user__img\"/>");
        html.append("                    <p class=\"comment__item__user__nickname\">account</p>");
        html.append("                </div>");
        html.append("                <p class=\"comment__item__article\">Comment 3</p>");
        html.append("            </li>");
        html.append("            <button id=\"show-all-btn\" class=\"btn btn_ghost btn_size_m\">");
        html.append("                모든 댓글 보기(3개)");
        html.append("            </button>");
        html.append("        </ul>");
        html.append("        <nav class=\"nav\">");
        html.append("            <ul class=\"nav__menu\">");
        html.append("                <li class=\"nav__menu__item\">");
        html.append("                    <a class=\"nav__menu__item__btn\" href=\"\">");
        html.append("                        <img class=\"nav__menu__item__img\" src=\"./img/ci_chevron-left.svg\"/>");
        html.append("                        이전 글");
        html.append("                    </a>");
        html.append("                </li>");
        html.append("                <li class=\"nav__menu__item\">");
        html.append("                    <a class=\"btn btn_ghost btn_size_m\">댓글 작성</a>");
        html.append("                </li>");
        html.append("                <li class=\"nav__menu__item\">");
        html.append("                    <a class=\"nav__menu__item__btn\" href=\"\">");
        html.append("                        다음 글");
        html.append("                        <img class=\"nav__menu__item__img\" src=\"./img/ci_chevron-right.svg\"/>");
        html.append("                    </a>");
        html.append("                </li>");
        html.append("            </ul>");
        html.append("        </nav>");
        html.append("    </div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
