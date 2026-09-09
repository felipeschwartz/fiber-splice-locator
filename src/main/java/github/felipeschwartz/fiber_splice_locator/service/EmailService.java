package github.felipeschwartz.fiber_splice_locator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Fiber Splice Locator - Código de redefinição de senha");
        message.setText("Use o código abaixo para redefinir sua senha. Ele expira em 15 minutos:\n\n" + code);
        mailSender.send(message);
    }

    @Async
    public void sendServiceOrderAssignedEmail(String to, Long serviceOrderId, String ceoBoxNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Fiber Splice Locator - Nova ordem de serviço atribuída a você");
        message.setText("Você recebeu uma nova ordem de serviço.\n\n" +
                "OS #" + serviceOrderId + " - CEO " + ceoBoxNumber + "\n\n" +
                "Acesse o aplicativo para ver os detalhes e iniciar o atendimento.");
        mailSender.send(message);
    }
}
