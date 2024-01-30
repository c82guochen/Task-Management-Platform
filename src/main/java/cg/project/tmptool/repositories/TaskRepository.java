package cg.project.tmptool.repositories;

import cg.project.tmptool.dto.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByProjectIdOrderByPriority(String projectId);
}