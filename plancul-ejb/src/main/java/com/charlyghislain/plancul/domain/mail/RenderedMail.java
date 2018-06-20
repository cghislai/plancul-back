package com.charlyghislain.plancul.domain.mail;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

public class RenderedMail {
    @Nullable
    private String from;
    @NotNull
    @Size(min = 1)
    private String to;
    @NotNull
    private String cc;
    @Nullable
    private String bcc;
    @Nullable
    private String subject;
    @Nullable
    private String htmlBody;
    @NotNull
    private String textBody;

    public Optional<String> getFrom() {
        return Optional.ofNullable(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @NotNull
    public String getTo() {
        return to;
    }

    public void setTo(@NotNull String to) {
        this.to = to;
    }

    @NotNull
    public Optional<String> getCc() {
        return Optional.ofNullable(cc);
    }

    public void setCc(@NotNull String cc) {
        this.cc = cc;
    }

    public Optional<String> getBcc() {
        return Optional.ofNullable(bcc);
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NotNull String subject) {
        this.subject = subject;
    }

    public Optional<String> getHtmlBody() {
        return Optional.ofNullable(htmlBody);
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    @NotNull
    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(@NotNull String textBody) {
        this.textBody = textBody;
    }
}
