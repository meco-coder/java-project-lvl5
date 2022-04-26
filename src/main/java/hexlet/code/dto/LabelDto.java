package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LabelDto {

    @Size(min = 1, max = 50)
    private String name;

}
