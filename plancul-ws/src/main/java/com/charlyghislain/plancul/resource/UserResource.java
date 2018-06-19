package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;
    @Inject
    private UserConverter userConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;

    @GET
    @Path("/{id}")
    public WsUser getUser(@PathParam("id") long id) {
        User user = userService.findUserById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsUser wsUser = userConverter.toWsEntity(user);
        return wsUser;
    }


}
