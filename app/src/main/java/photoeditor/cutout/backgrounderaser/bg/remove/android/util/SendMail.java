package photoeditor.cutout.backgrounderaser.bg.remove.android.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    String host, port, emailid,username, password;
    Properties props = System.getProperties();
    Session l_session = null;

    public SendMail() {
        host = "smtp.mail.yahoo.com";
        port = "465";
        emailid = "hamza.mughal5593@yahoo.com";
        username = "hamza.mughal5593";
        password = "1qaz2wsx!@#EDC";

        emailSettings();
        createSession();
        sendMessage("hamza.mughal5593@yahoo.com", "hamza.mughal5593@gmail.com","Test subject","Test mail with some random text");
    }

    public void emailSettings() {
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", port);
    }

    public void createSession() {
        l_session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        l_session.setDebug(true);
    }

    public boolean sendMessage(String emailFromUser, String toEmail, String subject, String messageText) {
        try {
            MimeMessage message = new MimeMessage(l_session);
            emailid = emailFromUser;
            message.setFrom(new InternetAddress(this.emailid));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setContent(messageText, "text/html");

            Transport.send(message);
            System.out.println("An email has been sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
