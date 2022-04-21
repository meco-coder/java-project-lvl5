package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping(path = "${base-url}" + "/labels")
public class LabelController {

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelService labelService;


    @GetMapping(path = "")
    public Iterable<Label> getLabels() {
        return labelRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Label getLabel(@PathVariable long id) {
        return labelRepository.findById(id).get();
    }

    @PostMapping(path = "")
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) throws NoSuchAlgorithmException, InvalidKeySpecException {

        return labelService.createNewLabel(labelDto);
    }

    @PutMapping(path = "/{id}")
    public Label updateTask(@PathVariable long id, @RequestBody @Valid LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }


    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable long id) {
        List<Task> ss = labelRepository.findById(id).get().getTasks();
        if (!labelRepository.findById(id).get().getTasks().isEmpty()) {
            throw new NotImplementedException("Метка имеет одну из задач, её нельзя удалить");
        }
        labelRepository.deleteById(id);
    }


}
