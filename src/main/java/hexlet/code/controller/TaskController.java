package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(path = "${base-url}" + "/tasks")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskService taskService;

    @GetMapping(path = "")
    public Iterable<Task> getTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @GetMapping(path = "/{id}")
    public Task getTask(@PathVariable long id) {
        return taskRepository.findById(id).get();
    }

    @PostMapping(path = "")
    public Task createTask(@RequestBody @Valid TaskDto taskDto) throws NoSuchAlgorithmException, InvalidKeySpecException {

        return taskService.createNewTask(taskDto);
    }

    @PutMapping(path = "/{id}")
    public Task updateTask(@PathVariable long id, @RequestBody @Valid TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @PreAuthorize("""
                @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
            """)
    @DeleteMapping(path = "/{id}")
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }


}
