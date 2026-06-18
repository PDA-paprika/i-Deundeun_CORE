create table email_verification
(
    email_verification_id bigint auto_increment
        primary key,
    created_at            datetime(6) not null,
    updated_at            datetime(6) not null,
    email                 varchar(30) not null,
    is_verified           bit         not null,
    verification_code     varchar(6)  not null
);