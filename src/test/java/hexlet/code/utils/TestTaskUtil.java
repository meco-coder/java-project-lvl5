package hexlet.code.utils;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static hexlet.code.utils.TestLabelUtils.TEST_LABEL;
import static hexlet.code.utils.TestLabelUtils.TEST_LABEL2;
import static hexlet.code.utils.TestTaskStatusUtils.TEST_TASK_STATUS;
import static hexlet.code.utils.TestTaskStatusUtils.TEST_TASK_STATUS2;
import static hexlet.code.utils.TestUserUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestTaskUtil {

    public static final String TEST_TASK_NAME = "Task 1";
    public static final String TEST_TASK_NAME2 = "Task 2";
    public static final String TEST_TASK_NAME3 = "Task 3";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

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


    public Task createNewTask() {
        return taskRepository.save(Task.builder()
                .name(TEST_TASK_NAME)
                .description("Example")
                .author(userRepository.findByEmail(TEST_USERNAME).get())
                .taskStatus(taskStatusRepository.findByName(TEST_TASK_STATUS).get())
                .executor(userRepository.findByEmail(TEST_USERNAME).get())
                .labels(Collections.singletonList((labelRepository.findByName(TEST_LABEL).get())))
                .build()
        );
    }

    public Task createNewTask2() {
        return taskRepository.save(Task.builder()
                .name(TEST_TASK_NAME2)
                .description("Example2")
                .author(userRepository.findByEmail(TEST_USERNAME).get())
                .taskStatus(taskStatusRepository.findByName(TEST_TASK_STATUS2).get())
                .executor(userRepository.findByEmail(TEST_USERNAME).get())
                .labels(Collections.singletonList((labelRepository.findByName(TEST_LABEL2).get())))
                .build()
        );
    }


    public ResultActions regTask(final TaskStatusDto dto, final String userEmail) throws Exception {
        final var request = post("/tasks")
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return testUtils.perform(request, userEmail);
    }

}
