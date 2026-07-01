package joney.board.common.outboxmessagerelay;

import lombok.Getter;

import java.util.List;
import java.util.stream.LongStream;

//샤드를 각 어플리케이션에 균등하게 할당하기 위한 클래스
@Getter
public class AssignedShard {
    private List<Long> shards; //할당된 샤드 번호들

    //실행되고 있는 appId, coordinate에 의해서 실행되고 있는 app목록들, 샤드의 개수 (나는 4개)
    public static AssignedShard of(String appId, List<String> appIds, long shardCount) {
        AssignedShard assignedShard = new AssignedShard();
        assignedShard.shards = assign(appId, appIds, shardCount); //샤드 할당
        return assignedShard;
    }

    private static List<Long> assign(String appId, List<String> appIds, long shardCount) {
        int appIndex = findAppIndex(appId, appIds);
        if (appIndex == -1) {
            return List.of(); //할당할 샤드가 없으므로 빈 리스트만 반환
        }

        //범위
        long start = appIndex * shardCount / appIds.size();
        long end = (appIndex + 1) * shardCount / appIds.size() - 1;

        return LongStream.rangeClosed(start, end).boxed().toList();
    }

    private static int findAppIndex(String appId, List<String> appIds) {
        for (int i=0; i < appIds.size(); i++) {
            if (appIds.get(i).equals(appId)) {
                return i; //인덱스 찾으면 반환
            }
        }
        return -1;
    }
}
