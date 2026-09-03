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

    // @Async: o envio via SMTP externo (ex.: Ethereal) pode levar vários
    // segundos (DNS + handshake TLS + conversa SMTP) — se isso rodasse na
    // mesma thread do request HTTP, o app podia estourar o timeout do
    // axios (15s) esperando a resposta, mesmo com o e-mail sendo enviado
    // com sucesso logo em seguida.
    @Async
    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Fiber Splice Locator - Código de redefinição de senha");
        message.setText("Use o código abaixo para redefinir sua senha. Ele expira em 15 minutos:\n\n" + code);
        mailSender.send(message);
    }
}
