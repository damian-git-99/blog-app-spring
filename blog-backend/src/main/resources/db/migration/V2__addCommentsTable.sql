CREATE TABLE IF NOT EXISTS blog_app.comments
(
    id       INT          NOT NULL AUTO_INCREMENT,
    message  VARCHAR(255) NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_id  INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);