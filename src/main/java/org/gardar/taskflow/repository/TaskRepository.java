package org.gardar.taskflow.repository;

import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStatus(TaskStatus status);

    @Query("select t from Task t left join fetch t.comments where t.id = :id")
    Optional<Task> findByIdWithComments(@Param("id") Long id);
}
