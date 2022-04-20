package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface TaskStatusService {

    TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto) throws NoSuchAlgorithmException, InvalidKeySpecException;

    TaskStatus updateTaskStatus(long id, TaskStatusDto taskStatusDto);
}
