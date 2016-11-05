package tanton.homehunter.mail;

import tanton.homehunter.config.EmailConfig;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mailer {

    private final EmailConfig emailConfig;
    private final Properties props;

    private Session session;


    public Mailer(final EmailConfig emailConfig) throws MessagingException {
        this.emailConfig = emailConfig;
        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.emailConfig.getSmtpHost());
        props.put("mail.smtp.port", this.emailConfig.getSmtpPort());
    }


    private Session getSession() {
        if (this.session == null) {
            this.session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                        }
                    });
        }
        return session;
    }

    public void sendMessage(final String toEmail, final String subject, final String body) throws MessagingException {
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(emailConfig.getUsername()));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);

        System.out.println("Done");
    }
}
