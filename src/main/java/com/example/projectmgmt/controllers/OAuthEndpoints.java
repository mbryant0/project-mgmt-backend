package com.example.projectmgmt.controllers;

import com.example.projectmgmt.models.User;
import com.example.projectmgmt.models.UserMinimum;
import com.example.projectmgmt.models.UserRoles;
import com.example.projectmgmt.services.OrganizationService;
import com.example.projectmgmt.services.RoleService;
import com.example.projectmgmt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
public class OAuthEndpoints
{
    /**
     * A method in this controller adds a new user to the application so needs access to User Services to do this.
     */
    @Autowired
    private UserService userService;

    /**
     * A method in this controller adds a new user to the application with the role User so needs access to Role Services to do this.
     */
    @Autowired
    private RoleService roleService;

    /**
     * Connect to the Token store so the application can remove the token
     */
    @Autowired
    private TokenStore tokenStore;


    /**
     * This endpoint always anyone to create an account with the default role of USER. That role is hardcoded in this method.
     *
     * @param httpServletRequest the request that comes in for creating the new user
     * @param newminuser         A special minimum set of data that is needed to create a new user
     * @return The token access and other relevant data to token access. Status of CREATED. The location header to look up the new user.
     * @throws URISyntaxException we create some URIs during this method. If anything goes wrong with that creation, an exception is thrown.
     */
    @PostMapping(value = "/createnewadmin",
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseEntity<?> addAdmin(
            HttpServletRequest httpServletRequest,
            @Valid
            @RequestBody
                    UserMinimum newminuser)
            throws
            URISyntaxException
    {
        // Create the user
        User newuser = new User();

        newuser.setEmailaddress(newminuser.getEmailaddress());
        newuser.setPassword(newminuser.getPassword());
        newuser.setFirstname(newminuser.getFirstname());
        newuser.setLastname(newminuser.getLastname());
        newuser.setOrganization(newminuser.getOrganization());

        // add the default role of user
        Set<UserRoles> newRoles = new HashSet<>();
        newRoles.add(new UserRoles(newuser,
                roleService.findByName("ADMIN")));
        newuser.setRoles(newRoles);

        newuser = userService.save(newuser);

        // set the location header for the newly created resource
        // The location comes from a different controller!
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserURI = ServletUriComponentsBuilder.fromUriString(httpServletRequest.getServerName() + ":" + httpServletRequest.getLocalPort() + "/users/user/{userId}")
                .buildAndExpand(newuser.getUserid())
                .toUri();
        responseHeaders.setLocation(newUserURI);

        // return the access token
        // To get the access token, surf to the endpoint /login (which is always on the server where this is running)
        // just as if a client had done this.
        RestTemplate restTemplate = new RestTemplate();
        String requestURI = "http://localhost" + ":" + httpServletRequest.getLocalPort() + "/login";

        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(acceptableMediaTypes);
        headers.setBasicAuth(System.getenv("OAUTHCLIENTID"),
                System.getenv("OAUTHCLIENTSECRET"));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type",
                "password");
        map.add("scope",
                "read write trust");
        map.add("username",
                newminuser.getEmailaddress());
        map.add("password",
                newminuser.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,
                headers);

        String theToken = restTemplate.postForObject(requestURI,
                request,
                String.class);

        return new ResponseEntity<>(theToken,
                responseHeaders,
                HttpStatus.CREATED);
    }
// c302356a-2320-43c0-8751-2c3d8de7a2d9

    @PostMapping(value = "/createnewinactive",
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseEntity<?> addInactive(
            HttpServletRequest httpServletRequest,
            @Valid
            @RequestBody
                    UserMinimum newminuser)
            throws
            URISyntaxException
    {
        // Create the user
        User newuser = new User();

        newuser.setEmailaddress(newminuser.getEmailaddress());
        newuser.setPassword(newminuser.getPassword());
        newuser.setFirstname(newminuser.getFirstname());
        newuser.setLastname(newminuser.getLastname());
        newuser.setOrganization(newminuser.getOrganization());

        // add the default role of user
        Set<UserRoles> newRoles = new HashSet<>();
        newRoles.add(new UserRoles(newuser,
                roleService.findByName("INACTIVE")));
        newuser.setRoles(newRoles);

        newuser = userService.save(newuser);

        // set the location header for the newly created resource
        // The location comes from a different controller!
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserURI = ServletUriComponentsBuilder.fromUriString(httpServletRequest.getServerName() + ":" + httpServletRequest.getLocalPort() + "/users/user/{userId}")
                .buildAndExpand(newuser.getUserid())
                .toUri();
        responseHeaders.setLocation(newUserURI);

        // return the access token
        // To get the access token, surf to the endpoint /login (which is always on the server where this is running)
        // just as if a client had done this.
        RestTemplate restTemplate = new RestTemplate();
        String requestURI = "http://localhost" + ":" + httpServletRequest.getLocalPort() + "/login";

        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(acceptableMediaTypes);
        headers.setBasicAuth(System.getenv("OAUTHCLIENTID"),
                System.getenv("OAUTHCLIENTSECRET"));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type",
                "password");
        map.add("scope",
                "read write trust");
        map.add("username",
                newminuser.getEmailaddress());
        map.add("password",
                newminuser.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,
                headers);

        String theToken = restTemplate.postForObject(requestURI,
                request,
                String.class);

        return new ResponseEntity<>(theToken,
                responseHeaders,
                HttpStatus.CREATED);
    }

    /**
     * Removes the token for the signed on user. The signed user will lose access to the application. They would have to sign on again.
     *
     * <br>Example: <a href="http://localhost:2019/logout">http://localhost:2019/logout</a>
     *
     * @param request the Http request from which we find the authorization header which includes the token to be removed
     */
    // yes, both endpoints are mapped to the same Java method! So, either one will work.
    @GetMapping(value = {"/oauth/revoke-token", "/logout"},
            produces = "application/json")
    public ResponseEntity<?> logoutSelf(HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null)
        {
            // find the token
            String tokenValue = authHeader.replace("Bearer",
                    "")
                    .trim();
            // and remove it!
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }

        return new ResponseEntity<>("Successfully logged out!",HttpStatus.OK);
    }
}
