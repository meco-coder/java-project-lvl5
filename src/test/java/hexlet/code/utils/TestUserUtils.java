package hexlet.code.utils;


import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import static hexlet.code.utils.TestUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUserUtils {

    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    public static final String TEST_USERNAME_3 = "email3email.com";

    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "password"
    );

    private final UserDto testRegistrationDto2 = new UserDto(
            TEST_USERNAME_2,
            "fname",
            "lname",
            "password"
    );

    private final UserDto testRegistrationDto3 = new UserDto(
            TEST_USERNAME_3,
            "",
            "",
            ""
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
   private TestUtils testUtils;

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public UserDto getTestRegistrationDto2() {
        return testRegistrationDto2;
    }

    public UserDto getTestRegistrationDto3() {
        return testRegistrationDto3;
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post("/users")
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return testUtils.perform(request);
    }


}
