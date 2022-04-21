package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    private String description;

    @JsonManagedReference
    @NotNull
    @ManyToOne
    @JoinColumn(name="task_status_id")
    private TaskStatus taskStatus;

    @JsonManagedReference
    @NotNull
    @ManyToOne
    @JoinColumn(name="author_id")
    private User author;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name="executor_id")
    private User executor;

    @ManyToMany()
    private List<Label> labels;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
