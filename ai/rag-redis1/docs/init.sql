create database if not exists `rag-redis` default character set utf8mb4 collate utf8mb4_general_ci;
use `rag-redis`;


CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     `conversation_id` VARCHAR(36) NOT NULL,
    `content` TEXT NOT NULL,
    `type` ENUM('USER', 'ASSISTANT', 'SYSTEM', 'TOOL') NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,

    INDEX `SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX` (`conversation_id`, `timestamp`)
    );
