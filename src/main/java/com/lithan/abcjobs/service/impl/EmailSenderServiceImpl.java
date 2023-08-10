package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.SendMailRequest;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.service.EmailSenderService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmailSenderServiceImpl implements EmailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abcjobsportal@lithan.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    @Override
    public void sendMailToUsers(SendMailRequest sendMailRequest, String username) {
        boolean isAdmin = userRepository.getUserByUsername(username).getRole().equals("ADMIN");
        if (!isAdmin) {
            throw new RefusedActionException("You're not allowed to send email!");
        }

        String[] userIds = sendMailRequest.getUserIds();
        List<Long> ids = new ArrayList<>();
        for (String id : userIds) {
            ids.add(Long.parseLong(id));
        }
        List<User> users = userRepository.findAllById(ids);

        for (User user : users) {
            sendMail(
                    user.getEmail(),
                    sendMailRequest.getSubject(),
                    sendMailRequest.getText()
            );
        }
    }
}
