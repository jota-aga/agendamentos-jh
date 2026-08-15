CREATE TABLE user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES user_role(id)
);

INSERT INTO user_role (nome)
VALUES
    ('ADMIN'),
    ('CLIENT');

INSERT INTO users (nome, email, senha, role_id)
VALUES
    ('admin', 'admin@email.com', '$2a$10$./UcgBBzCT47Es9gAloDB.JoPUasDwdHJ7wk8LfXRBAZ0XAyO441m', 1);