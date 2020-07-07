package com.wissenbaumllp.app.controller;

import com.wissenbaumllp.app.models.AppRole;
import com.wissenbaumllp.app.models.Role;
import com.wissenbaumllp.app.models.User;
import com.wissenbaumllp.app.payload.request.LoginRequest;
import com.wissenbaumllp.app.payload.request.SignupRequest;
import com.wissenbaumllp.app.payload.response.JwtResponse;
import com.wissenbaumllp.app.payload.response.MessageResponse;
import com.wissenbaumllp.app.repository.RoleRepository;
import com.wissenbaumllp.app.repository.UserRepository;
import com.wissenbaumllp.app.security.jwt.JwtUtils;
import com.wissenbaumllp.app.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wbapi/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/user/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect( Collectors.toList());

        String jwt = null;
        String compare = "ROLE_USER";
        try{
            if((roles.get(0)).equals(compare)){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                jwt = jwtUtils.generateJwtToken(authentication);
            }
        }catch (Exception e){
            System.out.println(e);
        }

        System.out.println("roles: " + roles);
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getMobile(),
                userDetails.getUsername(),
                roles));

        // new MessageResponse("User Loged In successfully!")

        /**
         * ResponseEntity.ok(new JwtResponse(jwt,
         *                 userDetails.getId(),
         *                 userDetails.getMobile(),
         *                 userDetails.getUsername(),
         *                 roles));
         */
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        if (userRepository.existsByMobile(signUpRequest.getMobile())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Mobile is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getMobile(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole( AppRole.ROLE_USER )
                    .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
            System.out.println("userRoleId: "+userRole.getId()+","+userRole.getRole());

            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @PostMapping("/admin/login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect( Collectors.toList());

        //System.out.println("roles: " + roles);

        String jwt = null;
        String compare = "ROLE_ADMIN";
        try{
            if((roles.get(0)).equals(compare)){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                jwt = jwtUtils.generateJwtToken(authentication);
            }
        }catch (Exception e){
            System.out.println(e);
        }

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getMobile(),
                userDetails.getUsername(),
                roles));
        // new MessageResponse("User Loged In successfully!");
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        if (userRepository.existsByMobile(signUpRequest.getMobile())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Mobile is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getMobile(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole( AppRole.ROLE_ADMIN )
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            System.out.println("adminRoleId: "+userRole.getId()+","+userRole.getRole());

            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
    }

    @PostMapping("/owner/login")
    public ResponseEntity<?> authenticateOwner(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect( Collectors.toList());

        //System.out.println("roles: " + roles);

        String jwt = null;
        String compare = "ROLE_OWNER";
        try{
            if((roles.get(0)).equals(compare)){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                jwt = jwtUtils.generateJwtToken(authentication);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getMobile(),
                userDetails.getUsername(),
                roles));
        // new MessageResponse("User Loged In successfully!");
    }

    @PostMapping("/owner/signup")
    public ResponseEntity<?> registerOwner(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        if (userRepository.existsByMobile(signUpRequest.getMobile())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Mobile is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getMobile(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole( AppRole.ROLE_OWNER )
                    .orElseThrow(() -> new RuntimeException("Error: Owner Role is not found."));
            System.out.println("ownerRoleId: "+userRole.getId()+","+userRole.getRole());

            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Owner registered successfully!"));
    }
}


/*
else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRole(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "owner":
                        Role modRole = roleRepository.findByRole(AppRole.ROLE_OWNER)
                                .orElseThrow(() -> new RuntimeException("Error: Owner Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByRole(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
                        roles.add(userRole);
                }
            });
        }
*/