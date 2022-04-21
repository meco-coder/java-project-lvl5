package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.NotImplementedException;
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

    @Operation(summary = "Get all users")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = User.class))
    ))
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
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """)
    public User updateUser(@PathVariable long id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("""
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """)
    public void deleteUser(@PathVariable final long id) {

        if (!userRepository.findById(id).get().getAuthorTasks().isEmpty() ||
                !userRepository.findById(id).get().getExecutorTasks().isEmpty()) {
            throw new NotImplementedException("Пользователь имеет одну из задач, его нельзя удалить");
        }

        userRepository.deleteById(id);
    }
}
