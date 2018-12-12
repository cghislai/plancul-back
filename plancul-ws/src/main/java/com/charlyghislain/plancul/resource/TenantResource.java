package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.WsTenantStats;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.*;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.stat.TenantStat;
import com.charlyghislain.plancul.domain.validation.ValidEmail;
import com.charlyghislain.plancul.service.TenantService;
import com.charlyghislain.plancul.service.TenantUpdateService;
import com.charlyghislain.plancul.service.UserUpdateService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tenant")
@RolesAllowed({ApplicationGroupNames.REGISTERED_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class TenantResource {

    @Inject
    private TenantService tenantService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private WsTenantConverter wsTenantConverter;
    @Inject
    private TenantUpdateService tenantUpdateService;
    @Inject
    private TenantRoleConverter tenantRoleConverter;
    @Inject
    private WsTenantStatConverter wsTenantStatConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @POST
    public WsTenant createTenant(WsTenant wsTenant) {
        Tenant tenant = tenantConverter.fromWsEntity(wsTenant);

        Tenant createdTenant = tenantUpdateService.createTenant(tenant);

        return wsTenantConverter.toWsEntity(createdTenant);
    }

    @GET
    @Path("/{id}")
    public WsTenant getTenant(@PathParam("id") long id) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsTenant wsTenant = wsTenantConverter.toWsEntity(tenant);
        return wsTenant;
    }


    @GET
    @Path("/{id}/stats")
    public WsTenantStats getTenantStats(@PathParam("id") long id) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);
        TenantStat tenantStat = tenantService.calcTenantStat(tenant);
        WsTenantStats wsTenantStats = wsTenantStatConverter.toWsTenantStat(tenantStat);
        return wsTenantStats;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsTenant> updateTenant(@PathParam("id") long id, @NotNull @Valid WsTenant wsTenant) {
        Tenant tenant = tenantConverter.fromWsEntity(wsTenant);
        try {
            Tenant savedTenant = tenantService.saveTenant(tenant);
            WsRef<WsTenant> reference = wsTenantConverter.reference(savedTenant);
            return reference;
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }

    @POST
    @Path("/{id}/role/{role}/invitation")
    public void inviteTenantUser(@PathParam("id") long id,
                                 @PathParam("role") String roleName,
                                 @NotNull @ValidEmail String userEmail) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);
        TenantRole tenantRole = tenantRoleConverter.toTenantRole(roleName)
                .orElseThrow(BadRequestException::new);

        userUpdateService.inviteUser(userEmail, tenant, tenantRole);
    }

    @POST
    @RolesAllowed(ApplicationGroupNames.ADMIN)
    @Path("/search")
    public WsSearchResult<WsTenant> searchTenants() {
        TenantFilter tenantFilter = new TenantFilter();
        SearchResult<Tenant> tenantResults = tenantService.findTenants(tenantFilter, pagination, acceptedLanguage);
        WsSearchResult<WsTenant> wsSearchResult = searchResultConverter.convertSearchResults(tenantResults, wsTenantConverter);
        return wsSearchResult;
    }

}
