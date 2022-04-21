package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;


    @Override
    public Label createNewLabel(LabelDto labelDto) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Label label = new Label();
        label.setName(labelDto.getName());
        labelRepository.save(label);
        return label;
    }

    @Override
    public Label updateLabel(long id, LabelDto labelDto) {

        Label updateLabel = labelRepository.findById(id).get();
        updateLabel.setName(labelDto.getName());

        return labelRepository.save(updateLabel);
    }
}
