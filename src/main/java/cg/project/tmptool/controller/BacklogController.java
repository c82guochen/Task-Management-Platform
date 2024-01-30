package cg.project.tmptool.controller;

import cg.project.tmptool.dto.Task;
import cg.project.tmptool.services.MapValidationErrorService;
import cg.project.tmptool.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/backlog")
public class BacklogController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping(value = "/{projectId}")
    public ResponseEntity<?> addTaskToBacklog(@Valid @RequestBody Task task, BindingResult result, @PathVariable String projectId) {

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);

        if (errorMap != null) {
            return errorMap;
        }

        Task newTask = taskService.addProjectTask(projectId, task);

        return new ResponseEntity<Task>(newTask, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{projectId}")
    public ResponseEntity<?> getTaskListByProjectId(@PathVariable String projectId) {
        return new ResponseEntity<List<Task>>(taskService.findTaskByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping(value = "/{projectId}/{taskSequence}")
    public ResponseEntity<?> getTaskByProjectSequence(@PathVariable String projectId, @PathVariable String taskSequence) {
        Task task = taskService.findTaskByProjectSequence(projectId, taskSequence);
        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    @PutMapping(value = "/{projectId}/{projectSequence}")
    public ResponseEntity<?> updateTaskByProjectSequence(@Valid @RequestBody Task task, BindingResult result, @PathVariable String projectId, @PathVariable String projectSequence) {

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);

        if (errorMap != null) {
            return errorMap;
        }

        Task updatedTask = taskService.updateTaskByProjectSequence(task, projectId, projectSequence);
        return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<?> deleteTaskListByProjectId(@PathVariable String projectId) {
        taskService.deleteAllTaskByProjectId(projectId);
        return new ResponseEntity<String>("All tasks in Project ID " + projectId.toUpperCase() + " was deleted", HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}/{taskSequence}")
    public ResponseEntity<?> deleteTaskByProjectSequence(@PathVariable String projectId, @PathVariable String taskSequence) {
        taskService.deleteTaskByProjectSequence(projectId, taskSequence);
        return new ResponseEntity<String>("Task sequence " + taskSequence.toUpperCase() + " in Project ID " + projectId.toUpperCase() + " was deleted", HttpStatus.OK);

    }
}