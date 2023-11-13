package com.snwm.englishbot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.PromotedChannel;

@Repository
public interface PromotedChannelRepository extends JpaRepository<PromotedChannel, Long> {
    Optional<PromotedChannel> findById(Long id);
}
