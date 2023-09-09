CREATE DATABASE IF NOT EXISTS blog_app;

CREATE TABLE IF NOT EXISTS blog_app.users
(
    id       INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS blog_app.posts
(
    id           INT           NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255)  NOT NULL,
    content      VARCHAR(8000) NOT NULL,
    summary      VARCHAR(255)  NOT NULL,
    image        VARCHAR(255),
    category     VARCHAR(255)  NOT NULL,
    time_to_read INT           NOT NULL,
    isPublish    BOOLEAN       NOT NULL,
    user_id      INT           NOT NULL,
    created_at   DATETIME      NOT NULL,
    updated_at   DATETIME      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- todo: favorite table user -> post: one to many
CREATE TABLE IF NOT EXISTS blog_app.favorite_posts
(
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);