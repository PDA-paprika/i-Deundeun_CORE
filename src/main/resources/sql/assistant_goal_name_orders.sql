use ideundeun;
CREATE TABLE `assistant_goal_name_orders`
(
    `id`         BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id`  BIGINT    NOT NULL,
    `goal_id`    BIGINT    NOT NULL,
    `sort_order` INT       NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL     DEFAULT NULL,
    CONSTRAINT `uq_parent_goal` UNIQUE (`parent_id`, `goal_id`),
    CONSTRAINT `FK_parents_TO_assistant_goal_name_orders` FOREIGN KEY (`parent_id`) REFERENCES `parents` (`id`),
    CONSTRAINT `FK_assistant_goal_names_TO_orders` FOREIGN KEY (`goal_id`) REFERENCES `assistant_goal_names` (`id`) ON DELETE CASCADE
);
