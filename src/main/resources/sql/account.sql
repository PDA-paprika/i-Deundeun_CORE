use ideundeun;
CREATE TABLE `account`
(
    `account_id`     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `child_id`       BIGINT      NULL,
    `parent_id`      BIGINT      NULL,
    `account_type`   VARCHAR(10) NOT NULL COMMENT 'PARENT | CHILD',
    `account_number` VARCHAR(20) NOT NULL,
    `available_amt`  BIGINT      NOT NULL DEFAULT 0 COMMENT '사용가능 현금 예수금',
    `created_at`     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP   NULL     DEFAULT NULL
);

ALTER TABLE `account`
    ADD CONSTRAINT `FK_children_TO_account` FOREIGN KEY (`child_id`) REFERENCES `children` (`id`);
ALTER TABLE `account`
    ADD CONSTRAINT `FK_parents_TO_account` FOREIGN KEY (`parent_id`) REFERENCES `parents` (`id`);
