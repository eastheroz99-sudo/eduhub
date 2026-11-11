-- 강의와 강의 수업 회차 (D-11)
CREATE TABLE course (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    title         VARCHAR(100)  NOT NULL,
    description   VARCHAR(1000),
    instructor_id BIGINT        NOT NULL,
    capacity      INT           NOT NULL,
    start_date    DATE          NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_course_title ON course (title);

CREATE TABLE course_session (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    course_id  BIGINT        NOT NULL,
    session_no INT           NOT NULL,
    title      VARCHAR(100)  NOT NULL,
    starts_at  DATETIME(6)   NOT NULL,
    ends_at    DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_course_session_no UNIQUE (course_id, session_no),
    CONSTRAINT fk_course_session_course FOREIGN KEY (course_id) REFERENCES course (id)
);
