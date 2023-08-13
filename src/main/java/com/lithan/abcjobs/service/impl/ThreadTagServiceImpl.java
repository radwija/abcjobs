package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadTag;
import com.lithan.abcjobs.repository.ThreadTagRepository;
import com.lithan.abcjobs.service.ThreadTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadTagServiceImpl implements ThreadTagService {
    @Autowired
    private ThreadTagRepository threadTagRepository;

    @Override
    public List<ThreadTag> findAllThreadTags() {
        return threadTagRepository.findAll();
    }

    @Override
    public List<ThreadTag> findThreadTagByTagName(String tagName) {
        return threadTagRepository.findAllByTagName(tagName);
    }


}
