package cg.project.tmptool.services;

import cg.project.tmptool.dto.Backlog;
import cg.project.tmptool.dto.Task;
import cg.project.tmptool.repositories.BacklogRepository;
import cg.project.tmptool.repositories.TaskRepository;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Task addProjectTask(String projectId, Task task) {
        Backlog backlog = backlogRepository.findByProjectId(projectId);
        task.setBacklog(backlog);

        Integer backlogSequence = backlog.getTaskSequence();
        backlogSequence++;
        backlog.setTaskSequence(backlogSequence);

        task.setProjectSequence(projectId + "-" + backlogSequence);
        task.setProjectId(projectId);

        if (task.getPriority() == null) {
            task.setPriority(3);
        }
        // StringHelper是自带的
        if (StringHelper.isEmpty(task.getStatus())) {
            task.setStatus("TO_DO");
        }

        return taskRepository.save(task);
    }
}