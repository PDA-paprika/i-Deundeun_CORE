use ideundeun;
CREATE TABLE `assistant_goal_names`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id`  BIGINT       NOT NULL,
    `goal_type1` TINYINT      NOT NULL,
    `goal_type2` TINYINT      NOT NULL,
    `goal_type3` TINYINT      NOT NULL,
    `name`       VARCHAR(100) NULL,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP    NULL     DEFAULT NULL
);

ALTER TABLE `assistant_goal_names`
    ADD CONSTRAINT `FK_parents_TO_assistant_goal_names_1` FOREIGN KEY (`parent_id`)
        REFERENCES `parents` (`id`) ON DELETE CASCADE;

