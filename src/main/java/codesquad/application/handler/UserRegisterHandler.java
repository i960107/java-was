package codesquad.application.processor;

import codesquad.application.processor.exception.ProcessorException;
import codesquad.application.processor.exception.ValidationException;
import codesquad.application.db.InMemoryUserRepository;
import codesquad.was.http.WasRequest;
import codesquad.was.http.WasResponse;
import codesquad.application.model.User;
import codesquad.was.server.RequestParamModelMapper;
import codesquad.was.server.RequestProcessor;
import codesquad.was.server.exception.MethodNotAllowedException;

public class UserRequestProcessor extends RequestProcessor {

    private final InMemoryUserRepository userRepository;

    public UserRequestProcessor() {
        this.userRepository = new InMemoryUserRepository();
    }

    @Override
    public void doPost(WasRequest request, WasResponse response) throws ProcessorException {
        User user = RequestParamModelMapper.map(request.getParameterMap(), User.class);
        validate(user);
        userRepository.save(user);
        response.sendRedirect("/index.html");
    }

    private void validate(User user) {
        if (user.getUsername() == null ||
                user.getPassword() == null ||
                user.getNickname() == null) {
            throw new ValidationException();
        }
    }

    @Override
    public void doGet(WasRequest request, WasResponse response) throws ProcessorException {
        throw new MethodNotAllowedException();
    }
}

