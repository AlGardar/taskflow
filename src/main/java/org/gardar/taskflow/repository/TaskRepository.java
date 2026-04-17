package org.gardar.taskflow.repository;

import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStatus(TaskStatus status);

    Page<Task> findAllByAuthorId(Long authorId, Pageable pageable);

    @Query("select t from Task t left join fetch t.comments where t.id = :id")
    Optional<Task> findByIdWithComments(@Param("id") Long id);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.comments WHERE t.id = :id AND t.author.id = :authorId")
    Optional<Task> findByIdAndAuthorIdWithComments(@Param("id") Long id, @Param("authorId") Long authorId);

    Optional<Task> findByIdAndAuthorId(Long id, Long authorId);

    boolean existsByIdAndAuthorId(Long id, Long authorId);
}
