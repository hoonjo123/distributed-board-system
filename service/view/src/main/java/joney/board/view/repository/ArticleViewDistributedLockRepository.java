package joney.board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {

    private final StringRedisTemplate redisTemplate;

    // 게시글 조회에 대해서 분산 락을 적용한다.
    // view::article::{article_id}::user::{user_id}::lock
    private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";

    public boolean lock(Long articleId, Long userId, Duration ttl) {
        String key = generateKey(articleId, userId);
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
    }

    private String generateKey(Long articleId, Long userId) {
        return KEY_FORMAT.formatted(articleId, userId);
    }
}
