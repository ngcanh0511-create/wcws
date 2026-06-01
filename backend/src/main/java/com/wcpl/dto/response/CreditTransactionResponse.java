package com.wcpl.dto.response;

import com.wcpl.entity.CreditTransaction;

import java.time.LocalDateTime;

public record CreditTransactionResponse(
        Long id,
        Integer amount,
        Integer balanceAfter,
        String type,
        Long referenceId,
        String description,
        LocalDateTime createdAt
) {
    public static CreditTransactionResponse from(CreditTransaction tx) {
        return new CreditTransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getType(),
                tx.getReferenceId(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}
