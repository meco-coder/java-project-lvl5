package hexlet.code.controller;


import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.impl.TaskStatusServiceImpl;
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

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "${base-url}" + "/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusServiceImpl taskStatusServiceImpl;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Operation(summary = "Get all Task Statuses")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping(path = "")
    public Iterable<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @Operation(summary = "Get Task Status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status found"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @GetMapping(path = "/{id}")
    public TaskStatus getTaskStatus(@Parameter(description = "Task status id") @PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }
    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Create new Task Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task Status created"),
            @ApiResponse(responseCode = "422", description = "incorrect Task Status data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@Parameter(description = "Task Status data to save")
                                       @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusServiceImpl.createNewTaskStatus(taskStatusDto);
    }
    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Update Task Status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status updated"),
            @ApiResponse(responseCode = "422", description = "incorrect Task Status data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task Status not found")
    })
    @PutMapping(path = "/{id}")
    public TaskStatus updateTaskStatus(@Parameter(description = "Id Task Status to update") @PathVariable long id,
                                       @Parameter(description = "Task Status data to update")
                                       @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusServiceImpl.updateTaskStatus(id, taskStatusDto);
    }
    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Delete task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task Status not found")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteTaskStatus(@Parameter(description = "Id Task Status to delete")
                                 @PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }
}
