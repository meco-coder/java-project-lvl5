package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestLabelUtils;
import hexlet.code.utils.TestTaskStatusUtils;
import hexlet.code.utils.TestTaskUtil;
import hexlet.code.utils.TestUserUtils;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static hexlet.code.utils.TestTaskUtil.TEST_TASK_NAME;
import static hexlet.code.utils.TestTaskUtil.TEST_TASK_NAME2;
import static hexlet.code.utils.TestTaskUtil.TEST_TASK_NAME3;
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
public class TaskControllerTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUserUtils userUtils;
    @Autowired
    private TestTaskStatusUtils taskStatusUtils;
    @Autowired
    private TestLabelUtils labelUtils;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TestTaskUtil taskUtil;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;


    @BeforeEach
    void before() throws Exception {
        userUtils.regDefaultUser();
        labelUtils.regDefaultTaskStatus();
        labelUtils.regLabel(labelUtils.getTestRegistrationDto2(), TEST_USERNAME);
        taskStatusUtils.regDefaultTaskStatus();
        taskStatusUtils.regTaskStatus(taskStatusUtils.getTestRegistrationDto2(), TEST_USERNAME);
        taskUtil.createNewTask();
        taskUtil.createNewTask2();
    }

    @AfterEach
    void after() {
        testUtils.tearDown();
    }

    @Test
    public void getAllTasks() throws Exception {

        final var response = testUtils.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(2);
    }

    @Test
    public void getTasksByPredicate() throws Exception {
        long taskStatusId = taskStatusUtils.getTaskStatusByName().getId();
        final String queryString = "?taskStatus=" + taskStatusId;
        final var response = testUtils.perform(get("/tasks" + queryString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task task = tasks.get(0);
        assertThat(task.getTaskStatus().getId())
                .isEqualTo(taskStatusRepository.findById(taskStatusId).get().getId());
//        assertThat(task.getExecutor().getId())
//                .isEqualTo(userRepository.findById(Long.parseLong("1")).get().getId());
//        assertThat(task.getLabels().get(0).getId())
//                .isEqualTo(labelRepository.findById(Long.parseLong("2")).get().getId());

    }

    @Test
    public void getTaskStatusById() throws Exception {
        final Long id = taskRepository.findByName(TEST_TASK_NAME).get().getId();
        final var response = testUtils.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task expectedTask = taskRepository.findByName(TEST_TASK_NAME).get();
        assertEquals(expectedTask.getId(), task.getId());

        assertEquals(expectedTask.getTaskStatus().getId(), task.getTaskStatus().getId());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getExecutor().getId(), task.getExecutor().getId());
        assertEquals(expectedTask.getLabels().get(0).getId(), task.getLabels().get(0).getId());
        assertEquals(expectedTask.getAuthor().getId(), task.getAuthor().getId());
    }

    @Test
    public void getTaskByIdFail() throws Exception {
        final Long id = Long.parseLong("100");
        final var response = testUtils.perform(get("/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    public void createTask() throws Exception {
        assertThat(taskRepository.count()).isEqualTo(2);

        TaskDto taskDto = TaskDto.builder()
                .name(TEST_TASK_NAME3)
                .description("Example 3")
                .taskStatusId(taskStatusUtils.getTaskStatusByName().getId())
                .executorId(userUtils.getUserByEmail(TEST_USERNAME).getId())
                .labelIds(List.of(labelUtils.getTaskStatusByName().getId()))
                .build();

        final var request = post("/tasks")
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request, TEST_USERNAME);

        assertThat(taskRepository.count()).isEqualTo(3);

    }

    @Test
    public void createTaskFail() throws Exception {
        assertThat(taskRepository.count()).isEqualTo(2);

        TaskDto taskDto = TaskDto.builder()
                .name("")
                .description("")
                .taskStatusId(Long.parseLong("100"))
                .executorId(Long.parseLong("100"))
                .build();

        final var request = post("/tasks")
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        assertThat(taskRepository.count()).isEqualTo(2);

        TaskDto taskDto2 = TaskDto.builder()
                .name(TEST_TASK_NAME3)
                .description("Example 3")
                .taskStatusId(taskStatusUtils.getTaskStatusByName().getId())
                .executorId(userUtils.getUserByEmail(TEST_USERNAME).getId())
                .labelIds(List.of(labelUtils.getTaskStatusByName().getId()))
                .build();

        final var request2 = post("/tasks")
                .content(asJson(taskDto2))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request2).andExpect(status().isUnauthorized());

        assertThat(taskRepository.count()).isEqualTo(2);

    }

    @Test
    public void updateTask() throws Exception {

        TaskDto taskDto = TaskDto.builder()
                .name(TEST_TASK_NAME3)
                .description("Example 3")
                .taskStatusId(taskStatusUtils.getTaskStatusByName().getId())
                .executorId(userUtils.getUserByEmail(TEST_USERNAME).getId())
                .labelIds(List.of(labelUtils.getTaskStatusByName().getId()))
                .build();

        final Long idTask = taskRepository.findByName(TEST_TASK_NAME2).get().getId();

        final var request = put("/tasks/{id}", idTask)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(taskRepository.existsById(idTask));
        assertNull(taskRepository.findByName(TEST_TASK_NAME2).orElse(null));
        assertNotNull(taskRepository.findByName(TEST_TASK_NAME3).orElse(null));

    }

    @Test
    public void updateTaskFail() throws Exception {


        TaskDto taskDto = TaskDto.builder()
                .name("")
                .description("")
                .taskStatusId(Long.parseLong("100"))
                .executorId(Long.parseLong("100"))
                .build();

        final Long idTask = taskRepository.findByName(TEST_TASK_NAME2).get().getId();

        final var request = put("/tasks/{id}", idTask)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        TaskDto taskDto2 = TaskDto.builder()
                .name(TEST_TASK_NAME3)
                .description("Example 3")
                .taskStatusId(taskStatusUtils.getTaskStatusByName().getId())
                .executorId(userUtils.getUserByEmail(TEST_USERNAME).getId())
                .labelIds(List.of(labelUtils.getTaskStatusByName().getId()))
                .build();

        final var request2 = put("/tasks/{id}", idTask)
                .content(asJson(taskDto2))
                .contentType(APPLICATION_JSON);

        testUtils.perform(request2).andExpect(status().isUnauthorized());

    }

    @Test
    void taskDeleteById() throws Exception {

        assertEquals(2, taskRepository.count());
        final Long idTask = taskRepository.findByName(TEST_TASK_NAME2).get().getId();
        final var response = testUtils.perform(delete("/tasks/{id}", idTask),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(1, taskRepository.count());
    }

    @Test
    void taskDeleteByIdFail() throws Exception {
        assertEquals(2, taskRepository.count());
        final Long idTask = Long.parseLong("100");
        final var response = testUtils.perform(delete("/tasks/{id}", idTask),
                        TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertEquals(2, taskRepository.count());

        final var response2 = testUtils.perform(delete("/tasks/{id}", idTask))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertEquals(2, taskRepository.count());

    }


}
