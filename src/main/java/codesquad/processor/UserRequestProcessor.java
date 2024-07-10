package codesquad.processor;

import codesquad.db.UserRepository;
import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.model.User;
import codesquad.processor.exception.ProcessorException;
import codesquad.server.RequestParamModelMapper;
import codesquad.server.exception.MethodNotAllowedException;

public class UserRequestProcessor extends RequestProcessor {

    private final UserRepository userRepository;

    public UserRequestProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doPost(WasRequest request, WasResponse response) throws ProcessorException {
        User user = RequestParamModelMapper.map(request.getParameterMap(), User.class);
        userRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(WasRequest request, WasResponse response) throws ProcessorException {
        throw new MethodNotAllowedException();
    }
}

