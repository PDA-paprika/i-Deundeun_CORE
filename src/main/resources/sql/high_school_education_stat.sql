use ideundeun;
CREATE TABLE `high_school_education_stat`
(
    `education_stat_id`        BIGINT   NOT NULL,
    `school_level`             TINYINT  NOT NULL,
    `residence_region`         TINYINT  NOT NULL,
    `parent_economic_activity` TINYINT  NOT NULL,
    `monthly_household_income` TINYINT  NOT NULL,
    `desired_university_major` TINYINT  NOT NULL,
    `total_amount`             BIGINT   NOT NULL,
    `created_at`               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);