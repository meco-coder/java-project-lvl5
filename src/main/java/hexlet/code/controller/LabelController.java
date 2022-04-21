package hexlet.code.controller;

import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
    public void deleteLabel(@PathVariable long id) throws UnprocessableEntityException {

        try {
            labelRepository.deleteById(id);
        } catch (Exception e) {
            throw new UnprocessableEntityException("Нельзя удалить");
        }
    }


}
