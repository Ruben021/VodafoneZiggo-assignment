package nl.ruben.kinglouie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ruben.kinglouie.api.user.UsersApi;
import nl.ruben.kinglouie.model.user.User;
import nl.ruben.kinglouie.model.user.UsersResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int FIRST_PAGE = 1;

    final UsersApi usersApi;

    static Integer getTotalPages(final UsersResponse response) {
        return Optional.ofNullable(response)
                .map(UsersResponse::getTotalPages)
                .orElse(0);
    }

    static List<User> getData(final UsersResponse response) {
        return Optional.ofNullable(response)
                .map(UsersResponse::getData)
                .orElse(Collections.emptyList());
    }

    public User getUserToCreateOrderFor(final String email) {
        return findUserByEmail(email, FIRST_PAGE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    Optional<User> findUserByEmail(final String email, final int pageNumber) {
        UsersResponse usersResponse = usersApi.usersGet(pageNumber, null);
        Optional<User> userToCreateOrderFor = getData(usersResponse).stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findAny();

        if (userToCreateOrderFor.isEmpty() && getTotalPages(usersResponse) > pageNumber) {
            return findUserByEmail(email, pageNumber + 1);
        }
        return userToCreateOrderFor;
    }
}
