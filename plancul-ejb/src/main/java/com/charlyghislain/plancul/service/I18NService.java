package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.i18n.Language;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class I18NService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;


    public Optional<LocalizedMessage> findLocalizedMessageById(long id) {
        LocalizedMessage found = entityManager.find(LocalizedMessage.class, id);
        return Optional.ofNullable(found);
    }


    public LocalizedMessage createLocalizedMessage(Language language, String label) {
        LocalizedMessage localizedMessage = new LocalizedMessage();
        localizedMessage.setLabel(label);
        localizedMessage.setLanguage(language);

        LocalizedMessage managedMessage = entityManager.merge(localizedMessage);
        return managedMessage;
    }
}
