package tanton.homehunter;

import tanton.homehunter.mail.Mailer;

import javax.mail.MessagingException;

public class Main {

    public static void main(final String[] args) throws MessagingException {
        final Mailer mailer = new Mailer();

        StringBuilder msg = new StringBuilder();
        msg.append("Testing\nThis is a test message.\n\nMany thanks\nPete Tanton");
        
        mailer.sendMessage("pete.tanton@streamingrocket.com", "Test Message", msg.toString());
    }
}
