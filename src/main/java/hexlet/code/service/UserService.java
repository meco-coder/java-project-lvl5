package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface UserService {

    User createNewUser(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException;

    User updateUser(long id, UserDto userDto);
}
