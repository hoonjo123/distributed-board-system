비관적락 vs 낙관적락 vs 비동기

- 트랙잭션을 사용한다고 하더라도, 동시성 문제로 인해 구현 방법에 따라 데이터의 일관성은 깨질 수 있다.
- Lock을 사용하면 동시성 문제를 해결할 수 있을것같은데 지연처리되는 부분이 조금 걱정이다.

1. 비관적락

'데이터 접근 시에 충돌이 항상 발생한다고 가정하고, 데이터에 접근할 때 Lock을 걸어 \
다른 트랜잭션이 접근하지 못하도록 막는 방식이다.'

```aiignore
1. transaction start;
2. insert into article_like values({article_like_id}, {article_id}, {user_id},
{created_at});
• 좋아요 데이터 삽입
3. update article_like_count set like_count = like_count + 1 where article_id =
{article_id};
• 좋아요 수 데이터 갱신
• Pessimistic Lock 점유
4. commit;
• Pessimistic Lock 해제
```
- 데이터 베이스에 저장된 데이터를 기준으로 update문을 수행하는것이다.

```aiignore
1. transaction start;
2. insert into article_like values({article_like_id}, {article_id}, {user_id}, {created_at});
• 좋아요 데이터 삽입
3. select * from article_like_count where article_id = {article_id} for update;
• for update 구문으로 데이터 조회
• 조회된 데이터에 대해서 Pessimistic Lock 점유(이 시점부터 다른 Lock은 점유될 수 없다.)
• 애플리케이션에서 JPA를 사용하는 경우, 객체(엔티티)로 조회할 수 있다.
4. update article_like_count set like_count = {updated_like_count} where article_id = {article_id};
• 좋아요 수 데이터 갱신
• 조회된 데이터를 기반으로 새로운 좋아요 수를 만들어준다. (조회 시점부터 Lock을 점유하고 있기 때문에 가능)
• Client(애플리케이션)에서 JPA를 사용하는 경우, 엔티티로 위 과정을 수행할 수 있다.
5. commit;
• Pessimistic Lock 해제
```

for update구문으로 조회 결과에 대해서 락을 점유하겟다고 명시를 한다.
트랜젝션에 조회된 데이터를 기준으로 update문을 수행한다.


2. 낙관적락
항상 충돌이 없다고 가정한다.
각 테이블은 'version' 컬럼으로 데이터의 변경 여부를 추적한다
충돌을 감지하고 후처리를 위한 추가 작업이 필요하다

충돌 발생 -> commit 또는 rollback 또는 재시도


---
좋아요 수의 경우 트래픽이 크지 않기 때문에 비동기 방식은 제외하도록 한다.


---
테스트 코드 결과

pessimistic-lock-1 start\
lockType = pessimistic-lock-1, time = 17020ms\
pessimistic-lock-1 end\
count = 3001\
pessimistic-lock-2 start\
lockType = pessimistic-lock-2, time = 23994ms\
pessimistic-lock-2 end\
count = 3001\
optimistic-lock start\
lockType = optimistic-lock, time = 11716ms\
optimistic-lock end\
count = 366

낙관적락은 버젼에 따른 충돌감지로 인해 rollback을 해버려서 366개만 카운트됨

