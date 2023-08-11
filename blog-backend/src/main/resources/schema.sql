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
    id           INT          NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255) NOT NULL,
    content      VARCHAR(255) NOT NULL,
    summary      VARCHAR(255) NOT NULL,
    image        VARCHAR(255),
    category     VARCHAR(255) NOT NULL,
    time_to_read INT          NOT NULL,
    isPublished  BOOLEAN      NOT NULL,
    user_id      INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

