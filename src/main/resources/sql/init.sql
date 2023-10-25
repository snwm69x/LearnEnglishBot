
CREATE INDEX idx_words_word_type ON words (word_type);
CREATE INDEX idx_words_word_level ON words (word_level);

CREATE INDEX idx_user_words_user_id ON user_words (user_id);
CREATE INDEX idx_user_words_word_id ON user_words (word_id);

CREATE INDEX idx_user_chat_id ON user_data (chat_id);

CREATE INDEX idx_user_word_stats_user_id ON user_word_stats(user_id);
CREATE INDEX idx_user_word_stats_word_id ON user_word_stats(word_id);

CREATE INDEX idx_user_word_stats_user_id_word_id ON user_word_stats (user_id, word_id);


-- вывод количества попыток пользователей с информацией о пользователе
-- SELECT user_data.chat_id, user_data.username, user_data.first_name, user_data.last_name, user_data.user_type, user_data.word_level, SUM(user_word_stats.correct_attempts + user_word_stats.incorrect_attempts) as total_attempts
-- FROM user_data
-- LEFT JOIN user_word_stats ON user_data.chat_id = user_word_stats.user_id
-- GROUP BY user_data.chat_id;

