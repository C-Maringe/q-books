package com.qbook.app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {
    private boolean valid;
    private long expiryDate;
    private long createdDate;
    private long redeemedDate = 0L;
    private boolean redeemed;
    private String voucherNumber;
}
