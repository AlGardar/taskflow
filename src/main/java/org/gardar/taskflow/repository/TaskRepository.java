package org.gardar.taskflow.repository;

import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllByStatus(TaskStatus status);
}
