package com.snwm.englishbot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snwm.englishbot.entity.PromotedChatId;

@Repository
public interface PromotedChatIdRepository extends JpaRepository<PromotedChatId, Long> {
    Optional<PromotedChatId> findFirstByOrderByIdAsc();
}
