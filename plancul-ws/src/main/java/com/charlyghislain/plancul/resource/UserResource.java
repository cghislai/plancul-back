package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.api.WsUser;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    @EJB
    private UserService userService;

    @Inject
    private UserConverter userConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;
    @Inject
    private List<UntypedSort> sortList;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @GET
    @Path("/{id}")
    public WsUser getUser(@PathParam("id") long id) {
        User user = userService.findUserById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsUser wsUser = userConverter.toWsEntity(user);
        return wsUser;
    }


}
