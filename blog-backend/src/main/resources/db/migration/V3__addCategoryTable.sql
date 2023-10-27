CREATE TABLE IF NOT EXISTS blog_app.categories
(
    id       INT          NOT NULL AUTO_INCREMENT,
    category  VARCHAR(255) NOT NULL,
    post_id  INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);