-- Create the gametime database for the Game-Time Spring Boot application
CREATE DATABASE IF NOT EXISTS gametime
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE gametime;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_username (username),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS games (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  username VARCHAR(50) NOT NULL,
  date DATE NOT NULL,
  timeslot VARCHAR(20) NOT NULL,
  game_id BIGINT NOT NULL,
  game_name VARCHAR(100) NOT NULL,
  note VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_reservations_date (date),
  KEY idx_reservations_user_id (user_id),
  CONSTRAINT fk_reservations_game FOREIGN KEY (game_id) REFERENCES games(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认游戏
INSERT INTO games (name, description) VALUES
  ('王者荣耀', '5V5英雄对战手游'),
  ('和平精英', '战术竞技手游'),
  ('原神', '开放世界冒险游戏'),
  ('英雄联盟', '5V5MOBA竞技网游'),
  ('永劫无间', '动作竞技端游')
ON DUPLICATE KEY UPDATE name = VALUES(name);
