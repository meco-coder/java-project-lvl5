package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto){
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        taskStatusRepository.save(taskStatus);
        return taskStatus;
    }

    @Override
    public TaskStatus updateTaskStatus(long id, TaskStatusDto taskStatusDto) {
        List<TaskStatus> updateTaskStatus = taskStatusRepository.findById(id).stream()
                .peek(x -> {
                    x.setName(taskStatusDto.getName());
                }).toList();

        return taskStatusRepository.save(updateTaskStatus.get(0));
    }
}
