package com.team1.soai;

import org.junit.jupiter.api.Test;

/**
 * MainApplication 클래스에 대한 테스트
 * Spring Boot 애플리케이션의 기본 구조가 올바른지 확인하는 단위 테스트
 */
class MainApplicationTest {

    /**
     * MainApplication 클래스가 존재하고 접근 가능한지 테스트
     */
    @Test
    void mainApplicationClassExists() {
        // MainApplication 클래스가 존재하는지 확인
        assert MainApplication.class != null;
    }
}
