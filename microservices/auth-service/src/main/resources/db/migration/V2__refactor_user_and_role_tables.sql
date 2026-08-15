ALTER TABLE user_role
RENAME TO roles;

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_roles
        FOREIGN KEY (role_id)
        REFERENCES roles(id),

    CONSTRAINT fk_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

INSERT INTO users_roles (user_id, role_id)
SELECT id, role_id
FROM users;

ALTER TABLE users
DROP FOREIGN KEY fk_users_role;

ALTER TABLE users
DROP COLUMN role_id;