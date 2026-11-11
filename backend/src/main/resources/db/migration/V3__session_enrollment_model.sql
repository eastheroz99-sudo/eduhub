-- 사내 교육과정 모델로 전환 (D-14, D-17, D-20, D-21). MySQL 8.4와 H2(MySQL 모드) 양쪽에서 실행된다.

-- 과정: 강사·정원·시작일을 빼고 분류를 넣는다. 정원·일정은 회차로 옮긴다.
ALTER TABLE course DROP CONSTRAINT fk_course_instructor;
ALTER TABLE course DROP COLUMN instructor_id;
ALTER TABLE course DROP COLUMN capacity;
ALTER TABLE course DROP COLUMN start_date;
ALTER TABLE course ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT '미분류';
ALTER TABLE course ALTER COLUMN category DROP DEFAULT;
UPDATE course SET status = 'OPEN' WHERE status = 'RECRUITING';
CREATE INDEX idx_course_category ON course (category);

-- 회차: 제목을 빼고 장소·정원·확정 인원·상태를 넣는다.
ALTER TABLE course_session DROP COLUMN title;
ALTER TABLE course_session ADD COLUMN location VARCHAR(100) NOT NULL DEFAULT '미정';
ALTER TABLE course_session ADD COLUMN capacity INT NOT NULL DEFAULT 0;
ALTER TABLE course_session ADD COLUMN confirmed_count INT NOT NULL DEFAULT 0;
ALTER TABLE course_session ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'OPEN';
ALTER TABLE course_session ALTER COLUMN location DROP DEFAULT;
ALTER TABLE course_session ALTER COLUMN capacity DROP DEFAULT;
ALTER TABLE course_session ADD CONSTRAINT ck_course_session_seats
    CHECK (confirmed_count >= 0 AND confirmed_count <= capacity);

-- 역할: 학생·강사 → 임직원
UPDATE users SET role = 'EMPLOYEE' WHERE role IN ('STUDENT', 'INSTRUCTOR');

-- 수강 신청: 강의 단위 → 회차 단위. 강의 단위 신청은 회차로 옮길 수 없으므로 테이블을 새로 만든다.
DROP TABLE enrollment;

CREATE TABLE enrollment (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    employee_id BIGINT       NOT NULL,
    session_id  BIGINT       NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    applied_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_enrollment_employee_session UNIQUE (employee_id, session_id),
    CONSTRAINT fk_enrollment_employee FOREIGN KEY (employee_id) REFERENCES users (id),
    CONSTRAINT fk_enrollment_session FOREIGN KEY (session_id) REFERENCES course_session (id)
);

CREATE INDEX idx_enrollment_session_status ON enrollment (session_id, status);
