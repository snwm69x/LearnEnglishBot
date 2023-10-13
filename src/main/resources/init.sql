
CREATE INDEX idx_words_word_type ON words (word_type);
CREATE INDEX idx_words_word_level ON words (word_level);

CREATE INDEX idx_user_words_user_id ON user_words (user_id);
CREATE INDEX idx_user_words_word_id ON user_words (word_id);

CREATE INDEX idx_user_chat_id ON user_data (chat_id);

CREATE INDEX idx_user_word_stats_user_id ON user_word_stats(user_id);
CREATE INDEX idx_user_word_stats_word_id ON user_word_stats(word_id);

CREATE INDEX idx_user_word_stats_user_id_word_id ON user_word_stats (user_id, word_id);