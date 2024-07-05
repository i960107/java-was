package codesquad.processor;

import codesquad.db.UserRepository;
import codesquad.http.HttpHeaders;
import codesquad.http.HttpMethod;
import codesquad.http.WasRequest;
import codesquad.http.WasResponse;
import codesquad.model.User;
import codesquad.processor.exception.ProcessorException;
import codesquad.server.RequestParamModelMapper;
import codesquad.server.exception.MethodNotAllowedException;

public class UserRequestProcessor implements RequestProcessor {
    private final UserRepository userRepository;

    public UserRequestProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void process(WasRequest request, WasResponse response) {
        if (request.getMethod().equals(HttpMethod.GET)) {
            try {
                User user = RequestParamModelMapper.map(request.getQueryString(), User.class);
                userRepository.save(user);
                response.sendRedirect(request.getProtocol(), HttpHeaders.getDefault(), "/index.html");
            } catch (Exception e) {
                throw new ProcessorException();
            }
        }
        throw new MethodNotAllowedException();
    }
}
