package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(path = "${base-url}" + "/users")

public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping(path = "")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping(path = "")
    public User createUser(@RequestBody @Valid UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userService.createNewUser(userDto);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("""
            @Repository.findById(#id).get().getEmail() == authentication.getName()
        """)
    public User updateUser(@PathVariable long id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("""
            @Repository.findById(#id).get().getEmail() == authentication.getName()
        """)
    public void deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }
}
