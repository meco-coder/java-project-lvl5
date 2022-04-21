package hexlet.code.service;


import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface TaskService {

    Task createNewTask(TaskDto taskDto) throws NoSuchAlgorithmException, InvalidKeySpecException;

    Task updateTask(long id, TaskDto taskDto);

}
