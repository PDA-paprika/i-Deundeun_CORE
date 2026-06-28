use ideundeun;
CREATE TABLE `goal_orders`
(
    `id`         BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `child_id`   BIGINT    NOT NULL,
    `goal_id`    BIGINT    NOT NULL,
    `sort_order` INT       NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL     DEFAULT NULL,
    CONSTRAINT `uq_child_goal` UNIQUE (`child_id`, `goal_id`),
    CONSTRAINT `FK_children_TO_goal_orders` FOREIGN KEY (`child_id`) REFERENCES `children` (`id`),
    CONSTRAINT `FK_goals_TO_goal_orders` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`id`) ON DELETE CASCADE
);
