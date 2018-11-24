package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * Holds references to users in the process of being added.
 */
@ApplicationScoped
public class UserCreationQueue {

    private final Map<String, User> usersByMail = Collections.synchronizedMap(new HashMap<>());


    public void putUser(String email, User user) {
        synchronized (usersByMail) {
            usersByMail.put(email, user);
        }
    }

    public Optional<User> getUser(String mail) {
        synchronized (usersByMail) {
            return Optional.ofNullable(usersByMail.get(mail));
        }
    }

    public void removeUser(String mail) {
        synchronized (usersByMail) {
            usersByMail.remove(mail);
        }
    }


}
