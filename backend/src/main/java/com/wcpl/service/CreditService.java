package com.wcpl.service;

import com.wcpl.dto.response.CreditTransactionResponse;
import com.wcpl.entity.CreditTransaction;
import com.wcpl.entity.User;
import com.wcpl.repository.CreditTransactionRepository;
import com.wcpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditTransactionRepository txRepository;
    private final UserRepository userRepository;

    public Page<CreditTransactionResponse> getHistory(Long userId, Pageable pageable) {
        return txRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(CreditTransactionResponse::from);
    }

    @Transactional
    public void addCredits(User user, int amount, String type, Long referenceId, String description) {
        user.setCredits(user.getCredits() + amount);
        userRepository.save(user);

        CreditTransaction tx = new CreditTransaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setBalanceAfter(user.getCredits());
        tx.setType(type);
        tx.setReferenceId(referenceId);
        tx.setDescription(description);
        txRepository.save(tx);
    }

    @Transactional
    public void deductCredits(User user, int amount, String type, Long referenceId, String description) {
        user.setCredits(user.getCredits() - amount);
        userRepository.save(user);

        CreditTransaction tx = new CreditTransaction();
        tx.setUser(user);
        tx.setAmount(-amount);
        tx.setBalanceAfter(user.getCredits());
        tx.setType(type);
        tx.setReferenceId(referenceId);
        tx.setDescription(description);
        txRepository.save(tx);
    }
}
