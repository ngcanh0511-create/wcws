package com.wcpl.config;

import com.wcpl.entity.CreditTransaction;
import com.wcpl.entity.User;
import com.wcpl.repository.CreditTransactionRepository;
import com.wcpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CreditTransactionRepository txRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername("admin")) return;

        // Tạo admin mặc định lần đầu khởi động
        User admin = new User();
        admin.setUsername("admin");
        admin.setDisplayName("Admin");
        admin.setPasswordHash(passwordEncoder.encode("123qwe!@#"));
        admin.setRole("ADMIN");
        admin.setCredits(9999);
        admin.setIsLocked(false);
        userRepository.save(admin);

        // Ghi credit transaction ban đầu
        CreditTransaction tx = new CreditTransaction();
        tx.setUser(admin);
        tx.setAmount(9999);
        tx.setBalanceAfter(9999);
        tx.setType("INITIAL");
        tx.setDescription("Credit ban đầu admin");
        txRepository.save(tx);

        log.info("====================================================");
        log.info("  ADMIN mặc định đã được tạo:");
        log.info("    Username : admin");
        log.info("    Password : 123qwe!@#");
        log.info("  Đổi mật khẩu ngay sau khi đăng nhập lần đầu!");
        log.info("====================================================");
    }
}
