package com.team1.soai.service;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.dto.UserModifyDTO;
import com.team1.soai.dto.UserSelectListDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@Transactional
public class UserManagementServiceTest {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void getUsersForManagementTest() {

        List<UserSelectListDTO> result = userManagementService.getUsersForManagement(0, 10, null, false);

        log.info("반환 리스트: {}", result);
    }

    @Test
    void insertUserTest() {
        UserDTO user = new UserDTO();
        user.setUserId("userTest");
        user.setUserPassword("Pass1");
        user.setUserName("홍길동");
        user.setUserRole("ADMIN");

        userManagementService.insertUser(user);

        UserDTO savedUser = userService.selectUserById("userTest");
        assertThat(savedUser).isNotNull();
        assertThat(passwordEncoder.matches("Pass1", savedUser.getUserPassword())).isTrue();
        log.info("저장된 UserDTO: {}", savedUser);
    }

    @Test
    void modifyUserTest() {
        UserDTO user = new UserDTO();
        user.setUserId("userTest");
        user.setUserPassword("Pass1");
        user.setUserName("홍길동");
        user.setUserRole("ADMIN");

        userManagementService.insertUser(user);

        UserDTO savedUser = userService.selectUserById("userTest");

        log.info("저장된 UserDTO: {}", savedUser);

        UserModifyDTO modifyDTO = new UserModifyDTO();
        modifyDTO.setUserId("userTest");
        modifyDTO.setUserPassword("Pass2");
        modifyDTO.setUserName("김중정");
        modifyDTO.setUserRole("USER");

        userManagementService.modifyUser(modifyDTO);

        UserDTO updatedUser = userService.selectUserById("userTest");
        assertThat(updatedUser.getUserName()).isEqualTo("김중정");
        assertThat(passwordEncoder.matches("Pass2", updatedUser.getUserPassword())).isTrue();
        log.info("최종 UserDTO: {}", updatedUser);
    }

    /**
     * modifyUser - 비밀번호 미입력 시 기존 비밀번호 유지
     */
    @Test
    void modifyUserEmptyPasswordTest() {
        UserDTO user = new UserDTO();
        user.setUserId("userTest");
        user.setUserPassword("Pass1");
        user.setUserName("홍길동");
        user.setUserRole("ADMIN");

        userManagementService.insertUser(user);

        UserDTO savedUser = userService.selectUserById("userTest");

        log.info("저장된 UserDTO: {}", savedUser);

        UserModifyDTO modifyDTO = new UserModifyDTO();
        modifyDTO.setUserId("userTest");
        modifyDTO.setUserPassword("");
        modifyDTO.setUserName("김중정");
        modifyDTO.setUserRole("USER");

        userManagementService.modifyUser(modifyDTO);

        UserDTO updatedUser = userService.selectUserById("userTest");
        assertThat(updatedUser.getUserName()).isEqualTo("김중정");
        assertThat(passwordEncoder.matches("Pass1", updatedUser.getUserPassword())).isTrue();
        log.info("최종 UserDTO: {}", updatedUser);
    }

    /**
     * deleteUser 테스트
     */
    @Test
    void deleteUserTest() {

        UserDTO user = new UserDTO();
        user.setUserId("userTest");
        user.setUserPassword("Pass1");
        user.setUserName("홍길동");
        user.setUserRole("ADMIN");
        userManagementService.insertUser(user);

        userManagementService.deleteUser("userTest");

        // then
        UserDTO deletedUser = userService.selectUserById("userTest");
        assertThat(deletedUser).isNull();
    }
}
