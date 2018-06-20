package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.mail.RenderedMail;
import com.charlyghislain.plancul.domain.mail.template.AccountCreationInvitationTemplate;
import com.charlyghislain.plancul.domain.mail.template.AccountTemplate;
import com.charlyghislain.plancul.domain.mail.template.AppTemplate;
import com.charlyghislain.plancul.domain.mail.template.MailTemplate;
import com.charlyghislain.plancul.domain.mail.template.MessageTemplate;
import com.charlyghislain.plancul.domain.mail.template.UserTemplate;
import com.charlyghislain.plancul.domain.mail.template.VelocityTemplate;
import com.charlyghislain.plancul.domain.util.MailTemplateMetaProperties;
import com.charlyghislain.plancul.domain.util.PlanCulProperties;
import com.charlyghislain.plancul.domain.util.PlanCulPropertiesProvider;
import com.charlyghislain.plancul.domain.util.exception.MessageTemplateFileNotFoundException;
import com.charlyghislain.plancul.domain.util.exception.PlanCulRuntimeException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

@ApplicationScoped
public class MailTemplateService {

    private static final String TEMPLATES_PATH = "com/charlyghislain/plancul/template/";
    private static final String TEMPLATE_FILE_EXTENSION = ".vm";
    private static final String TEMPLATE_HTML_FILE_EXTENSION = ".html.vm";

    @Inject
    private PlanCulPropertiesProvider planCulPropertiesProvider;
    @Inject
    private UserService userService;

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        properties.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());


        Velocity.init(properties);
    }

    public RenderedMail renderMail(MailTemplate mailTemplate, Language language) {
        VelocityContext context = new VelocityContext();
        this.setupVelocityContext(context, mailTemplate);

        String templateName = this.getTemplateName(mailTemplate);
        Template textTemplateContent = this.readTemplateTextContent(templateName, language);
        String textContent = renderMailContent(context, textTemplateContent);

        Optional<String> htmlContentOptional = this.readTemplateOptionalHtmlContent(templateName, language)
                .map(content -> this.renderMailContent(context, content));

        Map<String, String> metaProperties = this.readMetaProperties(templateName, language, context);
        RenderedMail renderedMail = this.createMail(metaProperties, textContent, htmlContentOptional.orElse(null));
        return renderedMail;
    }

    private RenderedMail createMail(Map<String, String> metaProperties, String textContent) {
        return this.createMail(metaProperties, textContent, null);
    }

    private RenderedMail createMail(Map<String, String> metaProperties, String textContent, @Nullable String htmlContentOptional) {
        String from = this.readMailMetaProperty(metaProperties, MailTemplateMetaProperties.FROM);
        String to = this.readMailMetaProperty(metaProperties, MailTemplateMetaProperties.TO);
        String cc = this.readMailMetaProperty(metaProperties, MailTemplateMetaProperties.CC);
        String bcc = this.readMailMetaProperty(metaProperties, MailTemplateMetaProperties.BCC);
        String subject = this.readMailMetaProperty(metaProperties, MailTemplateMetaProperties.SUBJECT);

        RenderedMail renderedMail = new RenderedMail();
        renderedMail.setFrom(from);
        renderedMail.setTo(to);
        renderedMail.setCc(cc);
        renderedMail.setBcc(bcc);
        renderedMail.setSubject(subject);
        renderedMail.setTextBody(textContent);
        renderedMail.setHtmlBody(htmlContentOptional);
        return renderedMail;
    }

    private String renderMailContent(VelocityContext context, Template templateContent) {
        try {
            StringWriter stringWriter = new StringWriter();
            templateContent.merge(context, stringWriter);

            stringWriter.flush();
            String renderedContent = stringWriter.toString();
            return renderedContent;
        } catch (Exception e) {
            throw new PlanCulRuntimeException("Failed to render mail", e);
        }
    }


    public AccountCreationInvitationTemplate createAccountCreationInvitationTemplate(User user) {
        AppTemplate appTemplate = this.createAppTemplate(user.getLanguage());
        UserTemplate userTemplate = this.createUserTemplate(user);
        AccountTemplate accountTemplate = this.createAccountTemplate(user);

        AccountCreationInvitationTemplate template = new AccountCreationInvitationTemplate();
        template.setApp(appTemplate);
        template.setUser(userTemplate);
        template.setAccount(accountTemplate);
        return template;
    }

    private AccountTemplate createAccountTemplate(User user) {
        String accountInitializationUrl = userService.getAccountInitializationUrl(user);

        AccountTemplate accountTemplate = new AccountTemplate();
        accountTemplate.setInitAccountUrl(accountInitializationUrl);
        return accountTemplate;
    }

    private UserTemplate createUserTemplate(User user) {
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String fullName = user.getFirstName() + " " + user.getLastName();

        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setEmail(email);
        userTemplate.setFirstName(firstName);
        userTemplate.setLastName(lastName);
        userTemplate.setFullName(fullName);
        return userTemplate;
    }

    private AppTemplate createAppTemplate(Language language) {
        String appName = planCulPropertiesProvider.getValue(PlanCulProperties.APP_NAME, language);
        String appNoReplyMail = planCulPropertiesProvider.getValue(PlanCulProperties.APP_MAIL_FROM_NOREPLY, language);
        String appProjectUrl = planCulPropertiesProvider.getValue(PlanCulProperties.APP_PROJECT_URL, language);
        String appMailSignature = planCulPropertiesProvider.getValue(PlanCulProperties.APP_MAIL_SIGNATURE, language);

        AppTemplate appTemplate = new AppTemplate();
        appTemplate.setName(appName);
        appTemplate.setNoReplyEmail(appNoReplyMail);
        appTemplate.setProjectUrl(appProjectUrl);
        appTemplate.setMailSignature(appMailSignature);
        return appTemplate;
    }

    private Template readTemplateTextContent(String templateName, Language language) {
        String templateFilePath = TEMPLATES_PATH + templateName + language.getFileSuffix() + TEMPLATE_FILE_EXTENSION;
        try {
            return readTemplateContent(templateName, templateFilePath);
        } catch (MessageTemplateFileNotFoundException e) {
            throw new PlanCulRuntimeException("No text template found for " + templateFilePath, e);
        }
    }

    private Optional<Template> readTemplateOptionalHtmlContent(String templateName, Language language) {
        String templateFilePath = TEMPLATES_PATH + templateName + language.getFileSuffix() + TEMPLATE_HTML_FILE_EXTENSION;
        try {
            Template content = readTemplateContent(templateName, templateFilePath);
            return Optional.of(content);
        } catch (MessageTemplateFileNotFoundException e) {
            return Optional.empty();
        }
    }

    private Template readTemplateContent(String templateName, String templateFilePath) throws MessageTemplateFileNotFoundException {
        try {
            Template template = Velocity.getTemplate(templateFilePath);
            return template;
        } catch (ResourceNotFoundException | ParseErrorException e) {
            throw new MessageTemplateFileNotFoundException("Failed to read template content " + templateName, e);
        }
    }

    private String getTemplateName(MessageTemplate template) {
        VelocityTemplate velocityTemplate = template.getClass().getAnnotation(VelocityTemplate.class);
        if (velocityTemplate == null) {
            throw new PlanCulRuntimeException("Message template not annotated with @VelocityTemplate: " + template.getClass().getName());
        }
        String templateName = velocityTemplate.value();
        return templateName;
    }


    private void setupVelocityContext(VelocityContext context, MessageTemplate mailTemplate) {
        Field[] declaredFields = mailTemplate.getClass().getDeclaredFields();
        Arrays.stream(declaredFields)
                .forEach(field -> this.addFieldToVelocityContext(context, field, mailTemplate));
    }

    private void addFieldToVelocityContext(VelocityContext context, Field field, MessageTemplate mailTemplate) {
        try {
            Object value = field.get(mailTemplate);
            String name = field.getName();
            context.put(name, value);
        } catch (IllegalAccessException e) {
            throw new PlanCulRuntimeException("Failed to setup velocity context for field " + field, e);
        }
    }


    private Map<String, String> readMetaProperties(String templateName, Language language, VelocityContext context) {
        String bundleFilePath = TEMPLATES_PATH + templateName;
        ResourceBundle bundle = ResourceBundle.getBundle(bundleFilePath, language.getLocale());

        Map<String, String> properties = new HashMap<>();
        Collections.list(bundle.getKeys())
                .stream()
                .forEach(key -> this.appendMetaProperty(key, bundle, properties, context));
        return properties;
    }

    private void appendMetaProperty(String key, ResourceBundle bundle, Map<String, String> properties, VelocityContext context) {
        String bundleValue = bundle.getString(key);

        StringWriter stringWriter = new StringWriter();
        boolean evaluationSucceeded = Velocity.evaluate(context, stringWriter, "properties-template", bundleValue);
        if (!evaluationSucceeded) {
            throw new PlanCulRuntimeException("Failed to evaluate property " + key + " : " + bundleValue);
        }
        stringWriter.flush();
        String value = stringWriter.toString();
        properties.put(key, value);
    }


    private String readMailMetaProperty(Map<String, String> metaProperties, MailTemplateMetaProperties property) {
        boolean required = property.isRequired();
        String key = property.getKey();
        String value = metaProperties.get(key);
        if (required && value == null) {
            throw new PlanCulRuntimeException("Missing required mail meta property " + property);
        }
        return value;
    }

}
