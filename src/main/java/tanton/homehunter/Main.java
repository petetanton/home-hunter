package tanton.homehunter;

import com.google.gson.Gson;
import tanton.homehunter.config.Config;
import tanton.homehunter.mail.Mailer;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    public static void main(final String[] args) throws MessagingException, IOException {
        final Gson gson = new Gson();
        final Config config = gson.fromJson(readFile("config.json", UTF_8), Config.class);

        final Mailer mailer = new Mailer(config.getEmailConfig());

        StringBuilder msg = new StringBuilder();
        msg.append("Testing\nThis is a test message.\n\nMany thanks\nPete Tanton");

        mailer.sendMessage("pete.tanton@streamingrocket.com", "Test Message", msg.toString());
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
