TRUNCATE TABLE tasks CASCADE;

ALTER TABLE tasks ADD COLUMN author_id BIGINT NOT NULL ;

ALTER TABLE tasks ADD CONSTRAINT fk_tasks_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE ;

CREATE INDEX idx_tasks_author_id ON tasks(author_id);