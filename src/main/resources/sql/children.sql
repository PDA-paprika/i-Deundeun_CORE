use ideundeun;
create table children
(
    id                    bigint auto_increment
        primary key,
    parent_id             bigint                                not null,
    name                  varchar(50)                           not null,
    birth_date            date                                  not null comment '탐색탭 나이군 기준',
    gender                varchar(10)                           not null comment 'male|female',
    birth_order           int         default 1                 not null,
    securities_account    varchar(20)                           not null comment '자녀 증권 계좌번호',
    profile_image_url     varchar(500)                          null,
    customer_segment_code varchar(50)                           null comment '탐색탭 ETF 통계용 고객 유형군 코드',
    created_via           varchar(20) default 'onboarding'      not null comment 'onboarding|mypage',
    allowance_linked      tinyint(1)  default 0                 not null comment '아동수당 연결 여부',
    created_at            timestamp   default CURRENT_TIMESTAMP not null,
    updated_at            timestamp                             null,
    deleted_at            timestamp                             null,
    constraint FK_parents_TO_children_1
        foreign key (parent_id) references parents (id)
);

