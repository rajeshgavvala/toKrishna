package com.wissenbaumllp.app.services;

import com.wissenbaumllp.app.models.User;
import com.wissenbaumllp.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //System.out.println("username: " + username);

        boolean setFlag = true;
        boolean emailExists = userRepository.existsByEmail( username );
        User user = null;
        if(setFlag & emailExists){
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        }else{
            user = userRepository.findByMobile(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        }
        return UserDetailsImpl.build(user);
    }

}
