package cg.project.tmptool.services;

import cg.project.tmptool.dto.Backlog;
import cg.project.tmptool.dto.Project;
import cg.project.tmptool.dto.Task;
import cg.project.tmptool.exceptions.ProjectIdException;
import cg.project.tmptool.repositories.BacklogRepository;
import cg.project.tmptool.repositories.TaskRepository;
import cg.project.tmptool.exceptions.ProjectNotFoundException;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Task addProjectTask(String projectId, Task task) {
        try {
            Backlog backlog = backlogRepository.findByProjectId(projectId);
            task.setBacklog(backlog);
            Integer backlogSequence = backlog.getTaskSequence();
            // If it doesn't find a valid projectId, there will be a null pointer exception, then code jumps into catch statement
            backlogSequence++;
            backlog.setTaskSequence(backlogSequence);

            task.setProjectSequence(projectId + "-" + backlogSequence);
            task.setProjectId(projectId);

            if (task.getPriority() == null) {
                task.setPriority(3);
            }
            // 采用StringHelper中的isEmpty函数检查task
            if (StringHelper.isEmpty(task.getStatus())) {
                task.setStatus("TO_DO");
            }

            return taskRepository.save(task);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project " + projectId + " does not exist");
        }

    }

    public List<Task> findTaskByProjectId(String productId) {
        return taskRepository.findByProjectIdOrderByPriority(productId);
        //这里即使projectId为空，也只会返回一个empty list，符合逻辑，所以不需要throw exception
        //所以要注意什么需要exception handler什么时候不需要，就和recursion代码什么时候是base case一样
    }

    public Task findTaskByProjectSequence(String projectId, String projectSequence) {

        // Step - 1 -> project/backlog is existed
        Backlog backlog = backlogRepository.findByProjectId(projectId);
        if (backlog == null) {
            throw new ProjectNotFoundException("Project " + projectId + " does not existed");
        }

        // Step - 2 -> task is existed
        Task task = taskRepository.findByProjectSequence(projectSequence);
        if (task == null) {
            throw new ProjectNotFoundException("Task with project sequence id" + projectSequence + " does not existed");
        }

        // Step - 3 -> match the relationship
        if (!task.getProjectId().equals(projectId)) {
            throw new ProjectNotFoundException("Task " + projectSequence + " does not exist in the project: " + projectId);
        } // 因为可能进行了task的移除和添加
        return task;
    }

    public Task updateTaskByProjectSequence(Task updatedTask, String projectId, String projectSequence) {
        // re-use the previous method
        Task task = findTaskByProjectSequence(projectId, projectSequence);
        task = updatedTask;
        return taskRepository.save(task);
    }

    //关于删除后影响sequence的解决方法：
    // 1、每次删改后根据task的createdAt来自动排序，时间复杂度为nlogn，用Linked HashMap
    // 2、手动删除，更改关系，删掉Task的cascade关系，更改backlog与task的关系为REFRESH
    public void deleteTaskByProjectSequence(String projectId, String projectSequence) {
    // 直接删除 Task 时（使用taskRepository.delete()时），只会应用刷新操作到与之关联的 Backlog 实体，导致为了维护数据的一致性，被删除的task被重新加载回来。
    // 所以我我们要做的是将backlog取出来，再将其中的TaskList取出来进行操作，操作后更新backlog，以此来断开级联操作
        Backlog backlog = backlogRepository.findByProjectId(projectId);
        if (backlog == null){
            throw new ProjectIdException("Project " + projectId.toUpperCase() + " does not exist!");
        }
        Task task = taskRepository.findByProjectSequence(projectSequence);
        if (task == null){
            throw new ProjectNotFoundException("Task with project sequence id " + projectSequence + " does not exist!");
        }
        if (!task.getProjectId().equals(projectId)) {
            throw new ProjectNotFoundException("Task " + projectSequence + " does not exist in project " + projectId);
        }
        List<Task> taskList = backlog.getTaskList();
        taskList.remove(task);
        //关于这里还要再改改？
        // backlog.setTaskSequence(taskList.size());
        backlogRepository.save(backlog);
        taskRepository.delete(task);
    }

    public void deleteAllTaskByProjectId(String projectId) {
        Backlog backlog = backlogRepository.findByProjectId(projectId);
        if (backlog == null){
            throw new ProjectIdException("Project " + projectId.toUpperCase() + " does not exist!");
        }
        List<Task> tasks = backlog.getTaskList();
        tasks.clear();
        backlog.setTaskSequence(0);
        backlogRepository.save(backlog);
        taskRepository.deleteAll();
    }
}