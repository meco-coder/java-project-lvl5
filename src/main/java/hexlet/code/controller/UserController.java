package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.springframework.http.HttpStatus.CREATED;

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

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user found"),
            @ApiResponse(responseCode = "500", description = "user with that id not found")
    })
    @GetMapping(path = "/{id}")
    public User getUser(@Parameter(description = "User id") @PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "422", description = "incorrect User data")})
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public User createUser(@Parameter(description = "User data to save")
                           @RequestBody @Valid UserDto userDto)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userService.createNewUser(userDto);
    }

    @Operation(summary = "Update user by his id")
    @SecurityRequirement(name = "java_project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User with that id not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize("""
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """)
    public User updateUser(@Parameter(description = "Id of user to be updated") @PathVariable final long id,
                           @Parameter(description = "User data to be updated") @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @SecurityRequirement(name = "java_project")
    @Operation(summary = "Delete user by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("""
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """)
    public void deleteUser(@Parameter(description = "Id of user to be deleted") @PathVariable final long id) {
        userRepository.deleteById(id);
    }
}
