package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.api.WsUser;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

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

    @Override
    public Optional<Sort<User>> mapSort(UntypedSort untypedSort) {
        return Optional.empty();
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
