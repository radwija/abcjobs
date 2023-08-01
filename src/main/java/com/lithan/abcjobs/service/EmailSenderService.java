package com.lithan.abcjobs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface EmailSenderService {
    void sendActivationLink(String to, String subject, String body);
}
