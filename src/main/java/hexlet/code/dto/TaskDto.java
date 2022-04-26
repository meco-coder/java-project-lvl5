package hexlet.code.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Setter
@Getter
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
