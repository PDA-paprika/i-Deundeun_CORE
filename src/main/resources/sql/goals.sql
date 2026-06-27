use ideundeun;
CREATE TABLE `goals`
(
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id`           BIGINT        NOT NULL,
    `child_id`            BIGINT        NOT NULL,
    `goal_type1`          INT           NOT NULL COMMENT '대학등록금|고등등록금|고등사교육비|초등사교육비|custom',
    `goal_type2`          INT           NULL,
    `goal_type3`          INT           NULL COMMENT '임시',
    `goal_type4`          INT           NULL COMMENT '임시',
    `level`               TINYINT       NULL,
    `name`                VARCHAR(100)  NULL,
    `target_amount`       BIGINT        NOT NULL,
    `target_date`         DATE          NOT NULL,
    `achieved_pct`        DECIMAL(5, 2) NOT NULL DEFAULT 0,
    `recommended_amount`  BIGINT        NULL COMMENT '목표설정도우미 추천 금액',
    `recommendation_note` VARCHAR(1000) NULL COMMENT '목표설정도우미 결과 요약',
    `status`              VARCHAR(20)   NOT NULL DEFAULT 'active' COMMENT 'active|completed|cancelled',
    `created_at`          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          TIMESTAMP     NULL     DEFAULT NULL,
    `deleted_at`          TIMESTAMP     NULL
);

ALTER TABLE `goals`
    ADD CONSTRAINT `FK_parents_TO_goals_1` FOREIGN KEY (`parent_id`)
        REFERENCES `parents` (`id`);

ALTER TABLE `goals`
    ADD CONSTRAINT `FK_children_TO_goals_1` FOREIGN KEY (`child_id`)
        REFERENCES `children` (`id`);