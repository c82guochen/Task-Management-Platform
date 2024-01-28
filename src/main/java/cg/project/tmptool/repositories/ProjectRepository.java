package cg.project.tmptool.repositories;

import cg.project.tmptool.dto.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

//    @Override
//    Iterable<Project> findAllById(Iterable<Long> longs);

    // repositories works directly with JPA which can access DB by findBy + fieldName
    Project findByProjectId(String projectId);
}