package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.mail.RenderedMail;
import com.charlyghislain.plancul.domain.util.PlanCulProperties;
import com.charlyghislain.plancul.domain.util.PlanCulPropertiesProvider;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
public class MailService {

    @Resource(lookup = "mail/plancul-mail")
    private Session mailSession;
    @Inject
    private PlanCulPropertiesProvider planCulPropertiesProvider;

    @Asynchronous
    public void sendMail(RenderedMail mail) {
        if (mailSession == null) {
            throw new RuntimeException("No mail resource available to send mail");
        }
        MimeMessage mimeMessage = this.createMimeMessage(mail);
        if (this.shouldSendMail()) {
            this.sendMail(mimeMessage);
        }
    }

    private void sendMail(MimeMessage mimeMessage) {
        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private MimeMessage createMimeMessage(RenderedMail mail) {
        try {
            String subject = mail.getSubject();
            String textBody = mail.getTextBody();
            Optional<String> htmlBody = mail.getHtmlBody();
            String from = mail.getFrom().orElseGet(this::getDefaultFrom);
            String to = mail.getTo();
            String cc = mail.getCc().orElse("");
            String bcc = mail.getBcc().orElse("");

            MimeMessage mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(textBody, "UTF-8", "plain");
            multipart.addBodyPart(textBodyPart);
            htmlBody.ifPresent(body -> this.addHtmlBody(body, multipart));
            mimeMessage.setContent(multipart);

            this.parseAddresses(from)
                    .forEach(address -> addFrom(address, mimeMessage));
            this.parseAddresses(to).stream()
                    .filter(this::filterRecipient)
                    .forEach(address -> addRecipient(address, Message.RecipientType.TO, mimeMessage));
            this.parseAddresses(cc).stream()
                    .filter(this::filterRecipient)
                    .forEach(address -> addRecipient(address, Message.RecipientType.CC, mimeMessage));
            this.parseAddresses(bcc).stream()
                    .filter(this::filterRecipient)
                    .forEach(address -> addRecipient(address, Message.RecipientType.BCC, mimeMessage));

            return mimeMessage;
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to build mime message ", e);
        }
    }

    private void addFrom(Address address, MimeMessage mimeMessage) {
        Address[] addresses = {address};
        try {
            mimeMessage.addFrom(addresses);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to add from address " + address.toString(), e);
        }
    }

    private void addRecipient(Address address, Message.RecipientType recipientType, MimeMessage mimeMessage) {
        try {
            mimeMessage.addRecipient(recipientType, address);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to add recipient address " + address.toString(), e);
        }
    }

    private Set<Address> parseAddresses(String adresses) {
        String[] splitted = adresses.split(";");
        return Arrays.stream(splitted)
                .filter(s -> !s.isEmpty())
                .map(this::parseAddress)
                .collect(Collectors.toSet());
    }

    private InternetAddress parseAddress(String address) {
        try {
            InternetAddress internetAddress = new InternetAddress(address);
            internetAddress.validate();
            return internetAddress;
        } catch (AddressException e) {
            throw new RuntimeException("Failed to parse email address " + address, e);
        }
    }

    private String getDefaultFrom() {
        return planCulPropertiesProvider.getValue(PlanCulProperties.APP_MAIL_FROM_NOREPLY);
    }

    private void addHtmlBody(String body, Multipart multipart) {
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(body, "UTF-8", "text/html");
            multipart.addBodyPart(mimeBodyPart);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to add html body part", e);
        }
    }


    private boolean filterRecipient(Address address) {
        String whiteListRegex = planCulPropertiesProvider.getValue(PlanCulProperties.CONFIG_MAIL_RECIPIENT_WHITELIST_REGEX);
        Pattern pattern = Pattern.compile(whiteListRegex);
        Matcher matcher = pattern.matcher(address.toString());
        return matcher.matches();
    }


    private boolean shouldSendMail() {
        String enabledValue = planCulPropertiesProvider.getValue(PlanCulProperties.CONFIG_MAIL_ENABLED);
        return Boolean.parseBoolean(enabledValue);
    }


}
