package nl.ruben.kinglouie.service;

import nl.ruben.kinglouie.api.user.UsersApi;
import nl.ruben.kinglouie.model.user.User;
import nl.ruben.kinglouie.model.user.UsersResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int RANDOM_INT = new Random().nextInt(1000000);
    private static final String EMAIL = UUID.randomUUID().toString();

    @Mock
    private UsersApi usersApi;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("getUserToCreateOrderFor")
    class GetUserToCreateOrderFor {

        @Test
        @DisplayName("No user found, expect exception")
        void getUserToCreateOrderFor() {
            UsersResponse usersResponse = mock(UsersResponse.class);
            User user = mock(User.class);

            when(usersApi.usersGet(ONE, null)).thenReturn(usersResponse);
            when(usersResponse.getData()).thenReturn(List.of(user));
            when(user.getEmail()).thenReturn(EMAIL);

            Optional<User> userByEmail = userService.findUserByEmail(EMAIL, ONE);

            assertTrue(userByEmail.isPresent());
        }
    }

    @Nested
    @DisplayName("#findUserByEmail")
    class FindUserByEmail {

        @Test
        @DisplayName("Not found")
        void findUsersOnPageNotFound() {
            UsersResponse usersResponse = mock(UsersResponse.class);

            when(usersApi.usersGet(ONE, null)).thenReturn(usersResponse);
            when(usersResponse.getData()).thenReturn(Collections.emptyList());

            Optional<User> userByEmail = userService.findUserByEmail(EMAIL, ONE);

            assertAll(
                    () -> assertTrue(userByEmail.isEmpty()),
                    () -> verify(usersApi).usersGet(anyInt(), eq(null))
            );
        }

        @Test
        @DisplayName("Found in first page")
        void findUsersOnPageFirstPage() {
            UsersResponse usersResponse = mock(UsersResponse.class);
            User user = mock(User.class);

            when(usersApi.usersGet(ONE, null)).thenReturn(usersResponse);
            when(usersResponse.getData()).thenReturn(List.of(user));
            when(user.getEmail()).thenReturn(EMAIL);

            Optional<User> userByEmail = userService.findUserByEmail(EMAIL, ONE);

            assertAll(
                    () -> assertTrue(userByEmail.isPresent()),
                    () -> verify(usersApi).usersGet(anyInt(), eq(null))
            );
        }

        @Test
        @DisplayName("Found in second page")
        void findUsersOnPageSecondPage() {
            UsersResponse usersResponse1 = mock(UsersResponse.class);
            UsersResponse usersResponse2 = mock(UsersResponse.class);
            User user1 = mock(User.class);
            User user2 = mock(User.class);

            when(usersApi.usersGet(ONE, null)).thenReturn(usersResponse1);
            when(usersApi.usersGet(TWO, null)).thenReturn(usersResponse2);
            when(usersResponse1.getTotalPages()).thenReturn(TWO);
            when(usersResponse1.getData()).thenReturn(List.of(user1));
            when(usersResponse2.getData()).thenReturn(List.of(user2));
            when(user2.getEmail()).thenReturn(EMAIL);

            Optional<User> userByEmail = userService.findUserByEmail(EMAIL, ONE);

            assertAll(
                    () -> assertTrue(userByEmail.isPresent()),
                    () -> verify(usersApi, times(2)).usersGet(anyInt(), eq(null))
            );
        }
    }

    @Nested
    @DisplayName("#getTotalPages")
    class GetTotalPages {

        @Test
        @DisplayName("No response, expect 0 in return")
        void getTotalPagesNoResponse() {
            assertEquals(ZERO, UserService.getTotalPages(null));
        }

        @Test
        @DisplayName("No data in response, 0 list in return")
        void getTotalPagesNoTotalPagesInResponse() {
            UsersResponse response = new UsersResponse();

            assertEquals(ZERO, UserService.getTotalPages(response));
        }

        @Test
        @DisplayName("Expect 1 in return")
        void getTotalPages() {
            UsersResponse response = new UsersResponse();
            response.setTotalPages(RANDOM_INT);

            assertEquals(RANDOM_INT, UserService.getTotalPages(response));
        }
    }

    @Nested
    @DisplayName("#getData")
    class GetData {

        @Test
        @DisplayName("No response, expect empty list in return")
        void getDataNoResponse() {
            assertTrue(UserService.getData(null).isEmpty());
        }

        @Test
        @DisplayName("No data in response, expect empty list in return")
        void getDataNoDataInResponse() {
            UsersResponse response = new UsersResponse();

            assertTrue(UserService.getData(response).isEmpty());
        }

        @Test
        @DisplayName("Expect list with users in return")
        void getData() {
            UsersResponse response = new UsersResponse();
            response.setData(List.of(mock(User.class)));

            assertEquals(ONE, UserService.getData(response).size());
        }
    }
}