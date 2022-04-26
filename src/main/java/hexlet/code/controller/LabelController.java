package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "${base-url}" + "/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelService labelService;

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Get all labels")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Label.class))
    ))
    @GetMapping(path = "")
    public Iterable<Label> getLabels() {
        return labelRepository.findAll();
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "label found"),
            @ApiResponse(responseCode = "404", description = "label with that id not found")
    })
    @GetMapping(path = "/{id}")
    public Label getLabel(@Parameter(description = "label id") @PathVariable final long id) {
        return labelRepository.findById(id).get();
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created"),
            @ApiResponse(responseCode = "422", description = "incorrect label data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public Label createLabel(@Parameter(description = "Label data to save")
                             @RequestBody @Valid LabelDto labelDto) throws NoSuchAlgorithmException,
            InvalidKeySpecException {

        return labelService.createNewLabel(labelDto);
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "label updated"),
            @ApiResponse(responseCode = "422", description = "incorrect label data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "label not found")
    })
    @PutMapping(path = "/{id}")
    public Label updateTask(@Parameter(description = "Id label to update") @PathVariable final long id,
                            @Parameter(description = "Label data to update")
                            @RequestBody @Valid LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@Parameter(description = "Id label to delete") @PathVariable final long id) {
        labelRepository.deleteById(id);
    }


}
