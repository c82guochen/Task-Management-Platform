package cg.project.tmptool.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project Name is required")
    private String projectName;

    // 除了Column以外所有的annotation都是JVM处理的，将Java语言转换成了sql语言
    @NotBlank(message = "Project Identifier is required")
    @Size(min = 4, max = 6, message = "Please use 4 to 6 characters")
    // 但access DB这层是由JPA来处理的，没有annotation可以处理，只能customize exception
    @Column(updatable = false, unique = true)
    private String projectId;

    @NotBlank(message = "Project description is required")
    private String description;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date endDate;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date createAt;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date updateAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "project")
    // refactor the project and task to avoid returning details and lowering return speed
    @JsonIgnore
    private Backlog backlog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;

    private String projectOwner; // using username instead of user_id

    @PrePersist
    protected void onCreate() {
        this.createAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = new Date();
    }


}