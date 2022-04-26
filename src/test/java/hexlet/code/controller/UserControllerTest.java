package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUserUtils;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.utils.TestUserUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUserUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @Autowired
    private TestUserUtils userUtils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createUser() throws Exception {
        assertEquals(0, userRepository.count());
        userUtils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void createUserFail() throws Exception {
        assertEquals(0, userRepository.count());
        userUtils.regUser(userUtils.getTestRegistrationDto3()).andExpect(status().isUnprocessableEntity());
        assertEquals(0, userRepository.count());
    }

    @Test
    public void getAllUsers() throws Exception {
        userUtils.regDefaultUser();
        final var response = utils.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }


    @Test
    public void updateUser() throws Exception {
        userUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var updateRequest = put("/users/{id}", userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));
    }

    @Test
    public void updateUserFail() throws Exception {
        userUtils.regDefaultUser();
        userUtils.regUser(userUtils.getTestRegistrationDto2());

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto("example@ex.com", "new name", "new last name",
                "new pwd");

        final var updateRequest = put("/users/{id}", userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME_2).andExpect(status().isForbidden());

        final var userDto2 = new UserDto("", "", "",
                "");

        final var updateRequest2 = put("/users/{id}", userId)
                .content(asJson(userDto2))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest2, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void deleteUser() throws Exception {
        userUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        utils.perform(delete("/users/{id}", userId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }


    @Test
    public void deleteUserFails() throws Exception {
        userUtils.regDefaultUser();
        userUtils.regUser(userUtils.getTestRegistrationDto2());

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        utils.perform(delete("/users/{id}", userId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());

        assertEquals(2, userRepository.count());
    }

}
