use ideundeun;
CREATE TABLE `living_stat`
(
    `living_stat_id`           BIGINT   NOT NULL,
    `school_name`              CHAR(1)  NOT NULL,
    `residence_region`         CHAR(1)  NOT NULL,
    `father_age`               INT      NOT NULL,
    `mother_age`               INT      NOT NULL,
    `parent_economic_activity` CHAR(1)  NOT NULL,
    `monthly_household_income` CHAR(1)  NOT NULL,
    `total_amount`             BIGINT   NOT NULL,
    `created_at`               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);