package joney.board.common.outboxmessagerelay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRelayConstants {
    // 샤딩이 4개가 되어있는 상황으로 가정하고 구현
    public static final int SHARD_COUNT = 4;
}
