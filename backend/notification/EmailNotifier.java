package notification;

/**
 * Simple notifier hook for fraud alerts.
 * Currently logs to console; can be extended to use SMTP (JavaMail) if configured.
 */
public class EmailNotifier {
    private final String toAddress;
    private final String fromAddress;

    public EmailNotifier(String toAddress, String fromAddress) {
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
    }

    /**
     * Send a notification message. Currently prints to stdout.
     * Replace the body with real SMTP logic if mail settings are available.
     */
    public void send(String subject, String body) {
        System.out.println("[EMAIL NOTIFIER] To: " + toAddress + " | From: " + fromAddress
            + " | Subject: " + subject + " | Body: " + body);
        // Placeholder for real email sending (e.g., JavaMail):
        // Properties props = new Properties(); props.put(... SMTP settings ...);
        // Session session = Session.getInstance(props, new Authenticator() { ... });
        // MimeMessage message = new MimeMessage(session); message.setFrom(...); message.addRecipient(...);
        // message.setSubject(subject); message.setText(body); Transport.send(message);
    }

    public String getToAddress() {
        return toAddress;
    }
}

