package cg.project.tmptool.controller;

import cg.project.tmptool.dto.Project;
import cg.project.tmptool.services.MapValidationErrorService;
import cg.project.tmptool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping(value = "")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result) {
        // controller不做任何数据逻辑处理,只指派任务
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);

        if (errorMap != null) {
            return errorMap;
        }

        Project newProject = projectService.saveOrUpdateProject(project);
        return new ResponseEntity<Project>(newProject, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{projectId}")
    public ResponseEntity<?> getProjectByProjectId(@PathVariable String projectId) {
        Project project = projectService.findProjectById(projectId);
        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    //    @GetMapping(value = "/all")
//    public Iterable<Project> getAll() {
//        return projectService.findAllProjects();
//    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAll() {
        Map<String, Iterable<Project>> map = new HashMap<>();
        // We return an object here, like what we returned in above functions
        map.put("data", projectService.findAllProjects());
        return new ResponseEntity<Map<String, Iterable<Project>>>(map, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<?> deleteProjectByProjectId(@PathVariable String projectId) {
        projectService.deleteProjectByProjectId(projectId);
        // 因为此函数没有返回值，所以直接返回我们定义的值
        return new ResponseEntity<String>("Project ID " + projectId.toUpperCase() + " was deleted", HttpStatus.OK);
    }

    @PutMapping(value = "")
    // 在进行PUT操作的数据一定要带上id（数据库里的id），不然会被默认成POST操作
    public ResponseEntity<?> updateNewProject(@Valid @RequestBody Project project, BindingResult result) {

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);

        if (errorMap != null) {
            return errorMap;
        }

        Project newProject = projectService.saveOrUpdateProject(project);
        return new ResponseEntity<Project>(newProject, HttpStatus.CREATED);
    }
}