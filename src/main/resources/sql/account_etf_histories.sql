use ideundeun;
CREATE TABLE `account_etf_histories`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `account_id`      BIGINT       NOT NULL,
    `event_type`      VARCHAR(20)  NOT NULL COMMENT 'BUY | SELL | GIFT_ETF_OUT | GIFT_ETF_IN',
    `external_etf_id` VARCHAR(100) NOT NULL COMMENT 'market schema etfs.id',
    `qty_delta`       INT          NOT NULL COMMENT 'ETF 수량 변화 (+매수·입고 / -매도·출고)',
    `price`           BIGINT       NOT NULL COMMENT 'ETF 체결 또는 양도 단가',
    `reference_id`    VARCHAR(100) NULL     COMMENT 'gift_transfers.id or external execution id',
    `reference_type`  VARCHAR(20)  NULL     COMMENT 'GIFT | EXECUTION',
    `memo`            VARCHAR(500) NULL,
    `occurred_at`     TIMESTAMP    NOT NULL COMMENT '실제 발생 시각',
    `created_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'DB 삽입 시각'
);

ALTER TABLE `account_etf_histories`
    ADD CONSTRAINT `FK_account_TO_account_etf_histories` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`);
