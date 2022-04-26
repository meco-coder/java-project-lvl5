package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(path = "${base-url}" + "/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Get all tasks with predicate")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Task.class))
    ))
    @GetMapping(path = "")
    public Iterable<Task> getTasks(@Parameter(description = "Predicate for tasks")
                                   @QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task found"),
            @ApiResponse(responseCode = "404", description = "task with that id not found")
    })
    @GetMapping(path = "/{id}")
    public Task getTask(@Parameter(description = "task id") @PathVariable final long id) {
        return taskRepository.findById(id).get();
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "422", description = "incorrect User data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public Task createTask(@Parameter(description = "Task data to create") @RequestBody @Valid TaskDto taskDto)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return taskService.createNewTask(taskDto);
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task updated"),
            @ApiResponse(responseCode = "422", description = "incorrect task data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping(path = "/{id}")
    public Task updateTask(@Parameter(description = "Id task to update") @PathVariable final long id,
                           @Parameter(description = "Task data to update") @RequestBody @Valid TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PreAuthorize("""
                @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
            """)
    @DeleteMapping(path = "/{id}")
    public void deleteTask(@Parameter(description = "Id task to delete") @PathVariable final long id) {
        taskRepository.deleteById(id);
    }


}
