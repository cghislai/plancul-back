package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter implements ToWsDomainObjectConverter<User, WsUser> {

    @EJB
    private UserService userService;

    public User load(WsRef<WsUser> ref) {
        return userService.findUserById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsUser toWsEntity(User entity) {
        Long id = entity.getId();
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String email = entity.getEmail();

        WsUser wsUser = new WsUser();
        wsUser.setId(id);
        wsUser.setFirstName(firstName);
        wsUser.setLastName(lastName);
        wsUser.setEmail(email);
        return wsUser;
    }

    public void updateEntity(User entity, WsUser wsEntity) {
        String firstName = wsEntity.getFirstName();
        String lastName = wsEntity.getLastName();
        String email = wsEntity.getEmail();

        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmail(email);
    }

}
