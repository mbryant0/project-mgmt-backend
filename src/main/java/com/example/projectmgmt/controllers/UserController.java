package com.example.projectmgmt.controllers;

import com.example.projectmgmt.models.User;
import com.example.projectmgmt.services.OrganizationService;
import com.example.projectmgmt.services.ProjectService;
import com.example.projectmgmt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController
{
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProjectService projectService;

    // RETRIEVES A LIST OF ALL USERS IN APPLICATION (DELETE AFTER PROJECT COMPLETION)
    // http://localhost:2021/users/users
    @GetMapping(value = "/users")
    public ResponseEntity<?> listAllUsers()
    {
        List<User> myUsers = userService.findAllUsers();
        return new ResponseEntity<>(myUsers, HttpStatus.OK);
    }

    // RETRIEVES A LIST OF ALL USERS IN ORGANIZATION BY ORGANIZATION ID
    // http://localhost:2021/users/organization
    @GetMapping(value = "/organization")
    public ResponseEntity<?> listAllUsersByOrganization(@RequestParam UUID organizationId)
    {
        List<User> myUsers = userService.findAllUsersByOrganization(organizationService.findOrganizationByOrganizationID(organizationId));
        return new ResponseEntity<>(myUsers, HttpStatus.OK);
    }


    // RETRIEVES INFO ABOUT A SPECIFIC USER (BY ID)
    // http://localhost:2021/users/user/{USERID}
    @GetMapping(value = "/user/{userid}", produces = {"application/json"})
    public ResponseEntity<?> findUserById(
            @PathVariable long userid)
    {
        User rtnUser = userService.findUserById(userid);
        return new ResponseEntity<>(rtnUser, HttpStatus.OK);
    }

    // POSTS A NEW USER TO DATABASE
    // http://localhost:2021/users/user
    @PostMapping(value = "/user", consumes = {"application/json"})
    public ResponseEntity<?> addNewUser(@Valid @RequestBody User newUser)
    {
        newUser.setUserid(0);
        newUser = userService.save(newUser);

        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userid}")
                .buildAndExpand(newUser.getUserid())
                .toUri();
        responseHeaders.setLocation(newUserURI);
        return new ResponseEntity<>(newUser, responseHeaders, HttpStatus.CREATED);
    }

    // UPDATES AN EXISTING USER (BY ID)
    // http://localhost:2021/users/user/{USERID}
    @PatchMapping(value = "/user/{ids}", consumes = {"application/json"})
    public ResponseEntity<?> updateUser (@RequestBody User updateUser,
            @PathVariable List<Long> ids){
        for(Long id: ids){
            userService.update(updateUser, id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //RETRIEVES CURRENT USERS'S INFO
    // http://localhost:2021/users/getuserinfo
    @GetMapping(value = "/getuserinfo", produces = {"application/json"})
    public ResponseEntity<?> getCurrentUserInfo()
    {
        String emailaddress = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userService.findByEmailaddress(emailaddress);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}



