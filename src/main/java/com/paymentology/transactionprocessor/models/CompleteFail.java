package com.paymentology.transactionprocessor.models;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CompleteFail {
    private Transaction transaction;
    private Transaction transaction2;
}
