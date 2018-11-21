package com.charlyghislain.plancul.astronomy.client.almanac;

import com.charlyghislain.plancul.astronomy.client.almanac.config.ConfigConstants;
import com.charlyghislain.plancul.astronomy.client.almanac.domain.AlmanacApi;
import com.charlyghislain.plancul.astronomy.client.almanac.domain.Event;
import com.charlyghislain.plancul.astronomy.client.almanac.provider.LocalDateTimeConverterProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AlmanacClient {

    @Inject
    @ConfigProperty(name = ConfigConstants.ALMANAC_API_URL)
    private String apiUrl;
    private AlmanacApi almanacResource;

    @PostConstruct
    public void init() {
        try {
            this.almanacResource = RestClientBuilder.newBuilder()
                    .baseUrl(new URL(apiUrl))
                    // Ensure date are correctly serialized in query params.
                    // Not registering this cause seconds to not be always serialized :/
                    .register(LocalDateTimeConverterProvider.class)
                    .build(AlmanacApi.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> searchMoonPhaseEvents(LocalDateTime fromTime, LocalDateTime toTime) {
        try {
            return almanacResource.getMoonPhaseEvents(fromTime, toTime);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> saerchMoonZodiacEvents(LocalDateTime fromTime, LocalDateTime toTime) {
        try {
            return almanacResource.getMoonZodiacEvents(fromTime, toTime);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
