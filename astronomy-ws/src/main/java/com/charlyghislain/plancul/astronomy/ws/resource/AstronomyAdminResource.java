package com.charlyghislain.plancul.astronomy.ws.resource;

import com.charlyghislain.plancul.astronomy.cache.service.AstronomyEventDao;
import com.charlyghislain.plancul.astronomy.ws.AstronomyApplication;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.service.UserQueryService;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Path("/admin")
@RolesAllowed(AstronomyApplication.ROLE_USER)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AstronomyAdminResource {

    @Inject
    private AstronomyEventDao cacheDao;
    @Inject
    private UserQueryService userQueryService;

    @POST
    @Path("/event/cache/clear")
    public void clearEventCache(@QueryParam("year") Integer year) {
        userQueryService.getLoggedUser()
                .filter(User::isAdmin)
                .orElseThrow(() -> new NotAuthorizedException("Not authorized"));
        cacheDao.clearEvents(year);
    }

}
