package tanton.homehunter.config;

public class Config {
    private EmailConfig emailConfig;
    private ZooplaConfig zooplaConfig;
    private GoogleConfig googleConfig;

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public ZooplaConfig getZooplaConfig() {
        return zooplaConfig;
    }

    public void setZooplaConfig(ZooplaConfig zooplaConfig) {
        this.zooplaConfig = zooplaConfig;
    }

    public GoogleConfig getGoogleConfig() {
        return googleConfig;
    }

    public void setGoogleConfig(GoogleConfig googleConfig) {
        this.googleConfig = googleConfig;
    }
}
