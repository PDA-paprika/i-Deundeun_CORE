use ideundeun;
CREATE TABLE `execution_goal_links`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id`       BIGINT       NOT NULL,
    `child_id`        BIGINT       NULL,
    `goal_id`         BIGINT       NULL,
    `etf_history_id`  BIGINT       NOT NULL,
    `created_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `linked_at`       TIMESTAMP    NULL     COMMENT '자녀·목표 연결 완료 시각'
);

ALTER TABLE `execution_goal_links`
    ADD CONSTRAINT `FK_parents_TO_execution_goal_links` FOREIGN KEY (`parent_id`) REFERENCES `parents` (`id`);
ALTER TABLE `execution_goal_links`
    ADD CONSTRAINT `FK_children_TO_execution_goal_links` FOREIGN KEY (`child_id`) REFERENCES `children` (`id`);
ALTER TABLE `execution_goal_links`
    ADD CONSTRAINT `FK_goals_TO_execution_goal_links` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`id`);

ALTER TABLE `execution_goal_links`
    ADD CONSTRAINT `FK_account_etf_histories_TO_execution_goal_links` FOREIGN KEY (`etf_history_id`) REFERENCES `account_etf_histories` (`id`);
