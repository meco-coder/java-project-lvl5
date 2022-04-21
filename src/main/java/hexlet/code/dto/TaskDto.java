package hexlet.code.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class TaskDto {

    @Size(min = 1, max = 100)
    private String name;

    private String description;

    @NotNull
    private long executorId;

    @NotNull
    private long taskStatusId;

    private List labelIds;

}
