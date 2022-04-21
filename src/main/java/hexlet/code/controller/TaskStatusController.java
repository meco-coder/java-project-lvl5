package hexlet.code.controller;


import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusServiceImpl;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(path = "${base-url}" + "/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusServiceImpl taskStatusServiceImpl;

    @Autowired
    private TaskStatusRepository taskStatusRepository;


    @GetMapping(path = "")
    public Iterable<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public TaskStatus getTaskStatus(@PathVariable long id) {
        return taskStatusRepository.findById(id).get();
    }

    @PostMapping(path = "")
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        return taskStatusServiceImpl.createNewTaskStatus(taskStatusDto);
    }

    @PutMapping(path = "/{id}")
    public TaskStatus updateTaskStatus(@PathVariable long id, @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusServiceImpl.updateTaskStatus(id, taskStatusDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteTaskStatus(@PathVariable long id) {

        if (!taskStatusRepository.findById(id).get().getTasks().isEmpty()) {
            throw new NotImplementedException("Статус имеет одну из задач, его нельзя удалить");
        }

        taskStatusRepository.deleteById(id);
    }
}
