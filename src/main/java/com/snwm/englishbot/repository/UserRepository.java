package com.snwm.englishbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.snwm.englishbot.entity.User;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByChatId(Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE from user_words where user_id = :user_id and word_id = :word_id", nativeQuery = true)
    void deleteWordById(@Param("user_id") Long userId, @Param("word_id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE from user_words where user_id = :user_id", nativeQuery = true)
    void deleteAllWordsByUserId(@Param("user_id") Long userId);

    List<User> findAll();

    List<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsername(String username, Pageable pageable);

}
