package com.wcpl.service;

import com.wcpl.entity.CreditTransaction;
import com.wcpl.entity.User;
import com.wcpl.repository.CreditTransactionRepository;
import com.wcpl.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CreditTransactionRepository txRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreditService creditService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setCredits(1000);
        user.setDisplayName("Test User");
    }

    // ── addCredits ─────────────────────────────────────────────────────────────

    @Test
    void addCredits_updatesUserBalance() {
        creditService.addCredits(user, 500, "TOPUP", null, "Nạp tiền");

        assertThat(user.getCredits()).isEqualTo(1500);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addCredits_savesTransactionWithCorrectFields() {
        creditService.addCredits(user, 200, "WIN", 99L, "Thắng kèo");

        ArgumentCaptor<CreditTransaction> captor = ArgumentCaptor.forClass(CreditTransaction.class);
        verify(txRepository).save(captor.capture());

        CreditTransaction tx = Objects.requireNonNull(captor.getValue());
        assertThat(tx.getAmount()).isEqualTo(200);
        assertThat(tx.getBalanceAfter()).isEqualTo(1200);
        assertThat(tx.getType()).isEqualTo("WIN");
        assertThat(tx.getReferenceId()).isEqualTo(99L);
        assertThat(tx.getDescription()).isEqualTo("Thắng kèo");
    }

    @Test
    void addCredits_zeroAmount_balanceUnchanged() {
        creditService.addCredits(user, 0, "NOOP", null, "");

        assertThat(user.getCredits()).isEqualTo(1000);
    }

    // ── deductCredits ──────────────────────────────────────────────────────────

    @Test
    void deductCredits_updatesUserBalance() {
        creditService.deductCredits(user, 300, "BET", null, "Đặt cược");

        assertThat(user.getCredits()).isEqualTo(700);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deductCredits_savesTransactionWithNegativeAmount() {
        creditService.deductCredits(user, 400, "BET", 55L, "Đặt cược kèo");

        ArgumentCaptor<CreditTransaction> captor = ArgumentCaptor.forClass(CreditTransaction.class);
        verify(txRepository).save(captor.capture());

        CreditTransaction tx = Objects.requireNonNull(captor.getValue());
        assertThat(tx.getAmount()).isEqualTo(-400);
        assertThat(tx.getBalanceAfter()).isEqualTo(600);
        assertThat(tx.getType()).isEqualTo("BET");
        assertThat(tx.getReferenceId()).isEqualTo(55L);
    }

    @Test
    void deductCredits_moreThanBalance_allowsNegative() {
        // Service không chặn overdraft — admin có thể deduct tùy ý
        creditService.deductCredits(user, 2000, "ADMIN_DEDUCT", null, "Trừ admin");

        assertThat(user.getCredits()).isEqualTo(-1000);
    }

    // ── balanceAfter consistency ───────────────────────────────────────────────

    @Test
    void addThenDeduct_balanceIsConsistent() {
        creditService.addCredits(user, 500, "TOPUP", null, "");
        assertThat(user.getCredits()).isEqualTo(1500);

        creditService.deductCredits(user, 200, "BET", null, "");
        assertThat(user.getCredits()).isEqualTo(1300);
    }
}
