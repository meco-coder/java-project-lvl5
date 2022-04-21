package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface LabelService {
    Label createNewLabel(LabelDto labelDto) throws NoSuchAlgorithmException, InvalidKeySpecException;

    Label updateLabel(long id, LabelDto labelDto);
}
