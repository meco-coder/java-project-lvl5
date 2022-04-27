package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestLabelUtils;
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
import static hexlet.code.utils.TestLabelUtils.TEST_LABEL;
import static hexlet.code.utils.TestLabelUtils.TEST_LABEL3;
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
public class LabelControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUserUtils userUtils;

    @Autowired
    private TestLabelUtils labelUtils;

    @Autowired
    private TestUtils testUtils;


    @BeforeEach
    void before() throws Exception {
        userUtils.regDefaultUser();
        labelUtils.regDefaultTaskStatus();
        labelUtils.regLabel(labelUtils.getTestRegistrationDto2(),
                userUtils.getTestRegistrationDto().getEmail());
    }

    @AfterEach
    void after() {
        testUtils.tearDown();
    }


    @Test
    public void getAllLabels() throws Exception {

        final var response = testUtils.perform(get("/labels"),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels).hasSize(2);
    }

    @Test
    public void getLabelById() throws Exception {
        final Long idLabel = labelUtils.getTaskStatusByName().getId();
        final var response = testUtils.perform(get("/labels/{id}", idLabel),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(labelUtils.getTaskStatusByName().getId(), taskStatus.getId());
        assertEquals(labelUtils.getTaskStatusByName().getName(), taskStatus.getName());
    }

    @Test
    public void getLabelByIdFail() throws Exception {
        final Long idLabel = Long.parseLong("100");
        final var response = testUtils.perform(get("/labels/{id}", idLabel))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        final var response2 = testUtils.perform(get("/labels/{id}", idLabel),
                        TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();


    }

    @Test
    public void createLabel() throws Exception {
        assertEquals(2, labelRepository.count());
        labelUtils.regLabel(labelUtils.getTestRegistrationDto3(), TEST_USERNAME)
                .andExpect(status()
                        .isCreated());
        assertEquals(3, labelRepository.count());
    }

    @Test
    public void createLabelFail() throws Exception {
        assertEquals(2, labelRepository.count());

        labelUtils.regLabel(new LabelDto(""), TEST_USERNAME)
                .andExpect(status()
                        .isUnprocessableEntity());
        assertEquals(2, labelRepository.count());

        final var postRequest = post("/labels")
                .content(asJson(TEST_LABEL3))
                .contentType(APPLICATION_JSON);
        testUtils.perform(postRequest)
                .andExpect(status()
                        .isUnauthorized());
        assertEquals(2, labelRepository.count());
    }

    @Test
    public void updateLabel() throws Exception {

        final Long labelId = labelRepository.findByName(labelUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var updateRequest = put("/labels/{id}", labelId)
                .content(asJson(TEST_LABEL3))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(labelRepository.existsById(labelId));
        assertNull(labelRepository.findByName(TEST_LABEL).orElse(null));
        assertNotNull(labelRepository.findByName(TEST_LABEL3).orElse(null));
    }

    @Test
    public void updateLabelFails() throws Exception {
        final Long labelId = labelRepository.findByName(labelUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var updateRequest = put("/labels/{id}", labelId)
                .content(asJson(""))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        assertEquals(2, labelRepository.count());

        final var updateRequest2 = put("/labels/{id}", labelId)
                .content(asJson(TEST_LABEL3))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest2).andExpect(status().isUnauthorized());
    }

    @Test
    void labelDeleteById() throws Exception {
        assertEquals(2, labelRepository.count());
        final Long labelId = labelRepository.findByName(labelUtils.getTestRegistrationDto().getName())
                .get()
                .getId();
        final var response = testUtils.perform(delete("/labels/{id}", labelId),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(1, labelRepository.count());
    }

    @Test
    void labelDeleteByIdFails() throws Exception {

        assertEquals(2, labelRepository.count());
        final Long labelId = Long.parseLong("100");
        final var response = testUtils.perform(delete("/labels/{id}", labelId),
                        TEST_USERNAME)
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse();

        assertEquals(2, labelRepository.count());

        final var response2 = testUtils.perform(delete("/labels/{id}", labelId))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertEquals(2, labelRepository.count());



    }

}
