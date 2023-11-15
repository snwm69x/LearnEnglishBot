package com.snwm.englishbot.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snwm.englishbot.entity.PromotedChannel;
import com.snwm.englishbot.repository.PromotedChannelRepository;
import com.snwm.englishbot.service.PromotedChannelService;

@Service
public class PromotedChannelServiceImpl implements PromotedChannelService {

    @Autowired
    private PromotedChannelRepository promotedChannelRepository;

    @Override
    public PromotedChannel getChannel() {
        Optional<PromotedChannel> optionalPromotedChannel = promotedChannelRepository.findById(0L);
        if (optionalPromotedChannel.isPresent()) {
            return optionalPromotedChannel.get();
        } else {
            throw new RuntimeException("No promoted channel found");
        }
    }

    @Override
    public void setChannel(PromotedChannel promotedChannel) {
        Optional<PromotedChannel> optionalPromotedChannel = promotedChannelRepository.findById(0L);
        if (optionalPromotedChannel.isPresent()) {
            PromotedChannel existingChannel = optionalPromotedChannel.get();
            if (promotedChannel.getChannelDescription() != null
                    && promotedChannel.getChannelDescription().equals(existingChannel.getChannelDescription())) {
                existingChannel.setChannelDescription(promotedChannel.getChannelDescription());
            }
            if (promotedChannel.getChannelLink() != null
                    && promotedChannel.getChannelLink().equals(existingChannel.getChannelLink())) {
                existingChannel.setChannelLink(promotedChannel.getChannelLink());
            }
            if (promotedChannel.getChannelName() != null
                    && promotedChannel.getChannelName().equals(existingChannel.getChannelName())) {
                existingChannel.setChannelName(promotedChannel.getChannelName());
            }
            if (promotedChannel.getChatId() != null
                    && promotedChannel.getChatId().equals(existingChannel.getChatId())) {
                existingChannel.setChatId(promotedChannel.getChatId());
            }
            promotedChannelRepository.save(existingChannel);
        } else {
            throw new RuntimeException("No promoted channel found to update");
        }
    }

}
