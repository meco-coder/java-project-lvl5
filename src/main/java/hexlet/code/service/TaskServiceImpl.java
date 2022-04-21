package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    UserService userService;


    @Override
    public Task updateTask(long id, TaskDto taskDto) {
        List<Task> updateTask = taskRepository.findById(id).stream()
                .peek(x -> {
                    x.setName(taskDto.getName());
                    if (taskDto.getDescription() != null) {
                        x.setDescription(taskDto.getDescription());
                    }
                    x.setTaskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get());
                    if (taskDto.getExecutorId() != 0) {
                        x.setExecutor(userRepository.findById(taskDto.getExecutorId()).get());
                    }
                }).toList();

        List<Label> labels = new ArrayList<>();
        for (Object labelId: taskDto.getLabelIds()) {
            labels.add(labelRepository.findById((Long.parseLong(String.valueOf(labelId)))).get());
        }
        updateTask.get(0).setLabels(labels);


        return taskRepository.save(updateTask.get(0));
    }

    @Override
    public Task createNewTask(TaskDto taskDto) {
        final Task task = new Task();
        task.setName(taskDto.getName());
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        task.setTaskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get());
        task.setAuthor(userService.getCurrentUser());
        if (taskDto.getExecutorId() != 0) {
            task.setExecutor(userRepository.findById(taskDto.getExecutorId()).get());
        }
        List<Label> labels = new ArrayList<>();
        for (Object labelId: taskDto.getLabelIds()) {
            labels.add(labelRepository.findById((Long.parseLong(String.valueOf(labelId)))).get());
        }
        task.setLabels(labels);
        taskRepository.save(task);
        return task;
    }
}
