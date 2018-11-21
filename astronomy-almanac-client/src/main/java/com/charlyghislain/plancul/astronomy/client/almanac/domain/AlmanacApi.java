package com.charlyghislain.plancul.astronomy.client.almanac.domain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface AlmanacApi {

    @GET
    @Path("/moon/event/moon_phase")
    @Produces(MediaType.APPLICATION_JSON)
    List<Event> getMoonPhaseEvents(@QueryParam("start_time") LocalDateTime startTime,
                                   @QueryParam("end_time") LocalDateTime endTime);

    @GET
    @Path("/moon/event/moon_zodiac")
    @Produces(MediaType.APPLICATION_JSON)
    List<Event> getMoonZodiacEvents(@QueryParam("start_time") LocalDateTime startTime,
                                    @QueryParam("end_time") LocalDateTime endTime);

}
