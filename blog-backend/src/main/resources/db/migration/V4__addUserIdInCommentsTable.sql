ALTER TABLE blog_app.comments
    ADD user_id INT;

ALTER TABLE blog_app.comments
ADD CONSTRAINT FK_user_id
FOREIGN KEY (user_id) REFERENCES users (id);
