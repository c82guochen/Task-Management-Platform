package cg.project.tmptool.repositories;

import cg.project.tmptool.dto.Backlog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacklogRepository extends CrudRepository<Backlog, Long> {
    Backlog findByProjectId(String projectId);
}