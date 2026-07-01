package joney.board.common.outboxmessagerelay;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//살아있는 app들을 추적하고 관리하는 클래스
@Component
@RequiredArgsConstructor
public class MessageRelayCoordinator {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    private final String APP_ID = UUID.randomUUID().toString();

    private final int PING_INTERVAL_SECONDS = 3; //핑 3초마다
    private final int PING_FAILURE_THRESHOLD = 3; //핑 3번 쏘고 응답없으면 실패햇다고 판단할거임

    public AssignedShard assignShards() {
        return AssignedShard.of(APP_ID, findAppIds(), MessageRelayConstants.SHARD_COUNT);
    }

    private List<String> findAppIds() {
        return redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1).stream()
                .sorted()
                .toList();
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void ping() {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey();
            conn.zAdd(key, Instant.now().toEpochMilli(), APP_ID);
            conn.zRemRangeByScore(
                    key,
                    Double.NEGATIVE_INFINITY,
                    Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli()
            );
            return null;
        });
    }

    @PreDestroy
    public void leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID);
    }

    private String generateKey() {
        return "message-relay-coordinator::app-list::%s".formatted(applicationName);
    }
}
