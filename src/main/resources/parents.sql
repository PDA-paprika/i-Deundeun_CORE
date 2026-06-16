CREATE TABLE `parents`
(
    `id`                BIGINT          NOT NULL                AUTO_INCREMENT  PRIMARY KEY,
    `email`             VARCHAR(50)     NOT NULL                COMMENT 'Unique',
    `account_number`    VARCHAR(20)     NOT NULL                COMMENT '로그인용 계좌번호',
    `selected_child_id` CHAR(36)        NULL	                COMMENT '앱 마지막 선택 자녀',
    `password_hash`     VARCHAR(255)    NOT NULL,
    `name`              VARCHAR(50)     NOT NULL,
    `birth_date`        DATE            NOT NULL,
    `relation`          VARCHAR(10)     NOT NULL                COMMENT 'mom|dad enum',
    `region`            VARCHAR(30)     NOT NULL                COMMENT '탐색탭 고객유형군 기준 enum',
    `child_count`       INT             NOT NULL,
    `profile_image_url` VARCHAR(500)    NULL,
    `income_level`      VARCHAR(20)     NULL	                COMMENT '목표설정도우미 소득분위',
    `asset_range`       VARCHAR(30)     NULL	                COMMENT '목표설정도우미 자산규모',
    `education_heat`    INT             NULL	                COMMENT '목표설정도우미 교육열 1~5',
    `dual_income`       BOOLEAN         NULL	                COMMENT '맞벌이 여부',
    `cluster_value`     INT             NULL                    COMMENT '군집 유형(1-N)',
    `created_at`        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP       NULL	DEFAULT NULL,
    `deleted_at`        TIMESTAMP       NULL,
    `cert_file_url`     VARCHAR(500)    NOT NULL                COMMENT '가족관계증명서'
);
