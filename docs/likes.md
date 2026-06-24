### 게시글 좋아요 

- 각 사용자는 각 게시글에 1회 좋아요
- 각 사용자는 게시글마다 좋아요를 누를 수 있다. 취소도 가능하다.
- 게시글 ID + 사용자 ID로 유니크 인덱스를 만들면 손쉽게 구현할 수 있다.

```aiignore
create table article_like (
article_like_id bigint not null primary key,
article_id bigint not null,
user_id bigint not null,
created_at datetime not null
);
```
```aiignore
create unique index idx_article_id_user_id on article_like(article_id asc,
user_id asc);

```

