package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {

    Optional<TaskStatus> findByName(String name);

}
