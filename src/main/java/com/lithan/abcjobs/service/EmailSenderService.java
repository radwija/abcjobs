package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.payload.request.SendMailRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface EmailSenderService {
    void sendMail(String to, String subject, String body);

    void sendMailToUsers(SendMailRequest sendMailRequest, String username);
}
