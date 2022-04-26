package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestTaskStatusUtils;
import hexlet.code.utils.TestUserUtils;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.utils.TestTaskStatusUtils.TEST_TASK_STATUS;
import static hexlet.code.utils.TestTaskStatusUtils.TEST_TASK_STATUS3;
import static hexlet.code.utils.TestUserUtils.TEST_USERNAME;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class TaskStatusControllerTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUserUtils userUtils;

    @Autowired
    private TestTaskStatusUtils taskStatusUtils;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void before() throws Exception {
        userUtils.regDefaultUser();
        taskStatusUtils.regDefaultTaskStatus();
        taskStatusUtils.regTaskStatus(taskStatusUtils.getTestRegistrationDto2(),
                userUtils.getTestRegistrationDto().getEmail());
    }

    @AfterEach
    void after() {
        testUtils.tearDown();
    }

    @Test
    public void getAllTaskStatuses() throws Exception {

        final var response = testUtils.perform(get("/statuses"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatuses).hasSize(2);
    }

    @Test
    public void getTaskStatusById() throws Exception {
        final Long id = taskStatusUtils.getTaskStatusByName().getId();
        final var response = testUtils.perform(get("/statuses/{id}", id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(taskStatusUtils.getTaskStatusByName().getId(), taskStatus.getId());
        assertEquals(taskStatusUtils.getTaskStatusByName().getName(), taskStatus.getName());
    }

    @Test
    public void getTaskStatusByIdFail() throws Exception {
        final Long id = Long.parseLong("100");
        final var response = testUtils.perform(get("/statuses/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertEquals(2, taskStatusRepository.count());
        taskStatusUtils.regTaskStatus(taskStatusUtils.getTestRegistrationDto3(), TEST_USERNAME)
                .andExpect(status()
                        .isCreated());
        assertEquals(3, taskStatusRepository.count());
    }

    @Test
    public void createTaskStatusFail() throws Exception {
        assertEquals(2, taskStatusRepository.count());
        taskStatusUtils.regTaskStatus(new TaskStatusDto(""), TEST_USERNAME)
                .andExpect(status()
                        .isUnprocessableEntity());
        assertEquals(2, taskStatusRepository.count());

        final var postRequest = post("/statuses")
                .content(asJson(TEST_TASK_STATUS3))
                .contentType(APPLICATION_JSON);
        testUtils.perform(postRequest)
                .andExpect(status()
                        .isUnauthorized());
        assertEquals(2, taskStatusRepository.count());
    }

    @Test
    public void updateTaskStatus() throws Exception {

        final Long idTaskStatus = taskStatusRepository.findByName(taskStatusUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var updateRequest = put("/statuses/{id}", idTaskStatus)
                .content(asJson(TEST_TASK_STATUS3))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(taskStatusRepository.existsById(idTaskStatus));
        assertNull(taskStatusRepository.findByName(TEST_TASK_STATUS).orElse(null));
        assertNotNull(taskStatusRepository.findByName(TEST_TASK_STATUS3).orElse(null));
    }

    @Test
    public void updateTaskStatusFail() throws Exception {
        final Long idTaskStatus = taskStatusRepository.findByName(taskStatusUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var updateRequest = put("/statuses/{id}", idTaskStatus)
                .content(asJson(""))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        assertEquals(2, taskStatusRepository.count());

        final var updateRequest2 = put("/statuses/{id}", idTaskStatus)
                .content(asJson(TEST_TASK_STATUS3))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest2).andExpect(status().isUnauthorized());
    }

    @Test
    void taskStatusDeleteById() throws Exception {

        assertEquals(2, taskStatusRepository.count());
        final Long idTaskStatus = taskStatusRepository.findByName(taskStatusUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var response = testUtils.perform(delete("/statuses/{id}", idTaskStatus),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    void taskStatusDeleteByIdFail() throws Exception {
        assertEquals(2, taskStatusRepository.count());
        final Long idTaskStatus = Long.parseLong("100");
        final var response = testUtils.perform(delete("/statuses/{id}", idTaskStatus),
                        TEST_USERNAME)
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse();

        assertEquals(2, taskStatusRepository.count());

        final var response2 = testUtils.perform(delete("/statuses/{id}", idTaskStatus))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertEquals(2, taskStatusRepository.count());

    }

}





