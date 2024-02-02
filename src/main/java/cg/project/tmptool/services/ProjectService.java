package cg.project.tmptool.services;

import cg.project.tmptool.dto.Backlog;
import cg.project.tmptool.dto.Project;
import cg.project.tmptool.dto.User;
import cg.project.tmptool.exceptions.ProjectIdException;
import cg.project.tmptool.repositories.BacklogRepository;
import cg.project.tmptool.repositories.ProjectRepository;
import cg.project.tmptool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username) {
        try {
            // Find the user
            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectOwner(user.getUsername());

            project.setProjectId(project.getProjectId().toUpperCase());
            // 确定一个project是否已存在：查询数据库中id
            if (project.getId() == null) {
                // 若还没有存至数据库中，代表该project为新建的，为其初始化backlog
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectId(project.getProjectId().toUpperCase());
            } else {
                // 若已存在，在PUT过程中将原本的backlog存至新数据中
                project.setBacklog(backlogRepository.findByProjectId(project.getProjectId()));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            // 这里可以用断点来查看exception的内容
            throw new ProjectIdException("Project ID '" + project.getProjectId().toUpperCase() + "' is already existed");
        }

    }

    public Project findProjectById(String projectId) {
        Project project = projectRepository.findByProjectId(projectId);

        if (project == null) {
            throw new ProjectIdException("Project ID '" + projectId.toUpperCase() + "' does not exist");

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