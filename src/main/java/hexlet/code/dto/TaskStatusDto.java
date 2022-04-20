package hexlet.code.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TaskStatusDto {

    @NotBlank
    @Size(min = 1, max = 15)
    private String name;
}
