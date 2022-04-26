package hexlet.code.utils;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import static hexlet.code.utils.TestUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestTaskStatusUtils {

    public static final String TEST_TASK_STATUS = "New";
    public static final String TEST_TASK_STATUS2 = "In work";
    public static final String TEST_TASK_STATUS3 = "Testing";

    private final TaskStatusDto testRegistrationDto = new TaskStatusDto(TEST_TASK_STATUS);

    private final TaskStatusDto testRegistrationDto2 = new TaskStatusDto(
            TEST_TASK_STATUS2
    );

    private final TaskStatusDto testRegistrationDto3 = new TaskStatusDto(
            TEST_TASK_STATUS3
    );

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TestUserUtils userUtils;


    public TaskStatusDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public TaskStatusDto getTestRegistrationDto2() {
        return testRegistrationDto2;
    }

    public TaskStatusDto getTestRegistrationDto3() {
        return testRegistrationDto3;
    }

    public TaskStatus getTaskStatusByName() {
        return taskStatusRepository.findByName(getTestRegistrationDto().getName()).get();
    }

    public ResultActions regDefaultTaskStatus() throws Exception {
        return regTaskStatus(testRegistrationDto, userUtils.getTestRegistrationDto().getEmail());
    }

    public ResultActions regTaskStatus(final TaskStatusDto dto, final String userEmail) throws Exception {
        final var request = post("/statuses")
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return testUtils.perform(request, userEmail);
    }
}
