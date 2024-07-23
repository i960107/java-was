package codesquad.application.handler;

import codesquad.application.db.UserDao;
import codesquad.application.model.User;
import codesquad.application.util.RequestParamModelMapper;
import codesquad.was.http.HttpRequest;
import codesquad.was.http.HttpResponse;
import codesquad.was.http.HttpStatus;
import codesquad.was.server.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRegisterHandler extends Handler {

    private Logger log = LoggerFactory.getLogger(getClass());

    private final UserDao userDao;

    public UserRegisterHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = RequestParamModelMapper.map(request.getParameterMap(), User.class);
        if (!isValid(user)) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }
        userDao.save(user);
        log.info("new user : ", user);
        response.sendRedirect("/index.html");
    }

    private boolean isValid(User user) {
        return user.getUsername() != null &&
                user.getPassword() != null &&
                user.getNickname() != null;
    }
}

