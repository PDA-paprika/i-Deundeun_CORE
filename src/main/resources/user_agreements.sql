CREATE TABLE `user_agreements`
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_id`      BIGINT      NOT NULL,
    `agreement_type` VARCHAR(20) NOT NULL,
    `agreed`         BOOLEAN     NOT NULL,
    `agreed_at`      TIMESTAMP   NULL,
    `created_at`     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP   NULL     DEFAULT NULL,
    CONSTRAINT `fk_user_agreements_parent`
        FOREIGN KEY (`parent_id`) REFERENCES `parents` (`id`)
);

