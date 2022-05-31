package com.paymentology.transactionprocessor.models;


import com.paymentology.transactionprocessor.utils.LocalDateFormatter;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.NullString;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Transaction {


    @NullString(nulls = { "?", "-" })
    private String profileName;
    @Convert(conversionClass = LocalDateFormatter.class, args = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;
    private Double transactionAmount;
    private String transactionNarrative;
    private String transactionDescription;
    private String transactionId;
    private String transactionType;
    private String walletReference;


}
