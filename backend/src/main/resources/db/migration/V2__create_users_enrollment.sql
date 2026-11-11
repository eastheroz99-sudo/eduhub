-- 사용자와 수강 신청. course.instructor_id 외래 키도 여기서 건다 (D-12)
CREATE TABLE users (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    email      VARCHAR(100)  NOT NULL,
    password   VARCHAR(255)  NOT NULL,
    name       VARCHAR(50)   NOT NULL,
    role       VARCHAR(20)   NOT NULL,
    active     BIT           NOT NULL,
    created_at DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

ALTER TABLE course
    ADD CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES users (id);

CREATE TABLE enrollment (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    student_id BIGINT       NOT NULL,
    course_id  BIGINT       NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    applied_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_enrollment_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES course (id)
);
