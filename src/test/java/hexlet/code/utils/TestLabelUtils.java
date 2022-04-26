package hexlet.code.utils;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import static hexlet.code.utils.TestUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestLabelUtils {
    public static final String TEST_LABEL = "Bag";
    public static final String TEST_LABEL2 = "Fix";
    public static final String TEST_LABEL3 = "New Label";

    private final LabelDto testRegistrationDto = new LabelDto(TEST_LABEL);

    private final LabelDto testRegistrationDto2 = new LabelDto(TEST_LABEL2);

    private final LabelDto testRegistrationDto3 = new LabelDto(TEST_LABEL3);


    public LabelDto getTestRegistrationTaskStatusDto() {
        return testRegistrationDto;
    }

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TestUserUtils userUtils;

    public LabelDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public LabelDto getTestRegistrationDto2() {
        return testRegistrationDto2;
    }

    public LabelDto getTestRegistrationDto3() {
        return testRegistrationDto3;
    }

    public Label getTaskStatusByName() {
        return labelRepository.findByName(getTestRegistrationDto().getName()).get();
    }

    public ResultActions regDefaultTaskStatus() throws Exception {
        return regLabel(testRegistrationDto, userUtils.getTestRegistrationDto().getEmail());
    }

    public ResultActions regLabel(final LabelDto dto, final String userEmail) throws Exception {
        final var request = post("/labels")
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return testUtils.perform(request, userEmail);
    }


}
