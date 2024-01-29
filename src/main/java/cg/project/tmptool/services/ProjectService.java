package cg.project.tmptool.services;

import cg.project.tmptool.dto.Project;
import cg.project.tmptool.exceptions.ProjectIdException;
import cg.project.tmptool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project) {
        try {
            project.setProjectId(project.getProjectId().toUpperCase());
            return projectRepository.save(project);
        } catch (Exception e) {
            // 这里可以用断点来查看exception的内容
            throw new ProjectIdException("Project ID '" + project.getProjectId().toUpperCase() + "' is already existed");
        }
    }

    public Project findProjectById(String projectId) {
        Project project = projectRepository.findByProjectId(projectId);

        if (project == null) {
            throw new ProjectIdException("Project ID '" + projectId.toUpperCase() + "' does not existed");

        }

        return projectRepository.findByProjectId((projectId));
    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByProjectId(String projectId) {
        Project project = projectRepository.findByProjectId(projectId);

        if (project == null) {
            throw new ProjectIdException("Project ID '" + projectId.toUpperCase() + "' does not existed");
        }
        // delete()这里没有任何返回值
        projectRepository.delete(project);
    }
}