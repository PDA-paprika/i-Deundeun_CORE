use ideundeun;
CREATE TABLE `region_mapping`
(
    `id`               BIGINT      NOT NULL,
    `region`           VARCHAR(30) NOT NULL,
    `residence_region` TINYINT     NOT NULL,
    `urban_flag`       TINYINT     NOT NULL,
    `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);