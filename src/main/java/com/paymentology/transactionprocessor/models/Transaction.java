package com.paymentology.transactionprocessor.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paymentology.transactionprocessor.utils.LocalDateFormatter;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.NullString;
import com.univocity.parsers.annotations.Trim;
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

    //We make the fields public to allow us to use Reflection to access fields and their values
    @NullString(nulls = { "?", "-" })
    @Trim
    public String profileName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Convert(conversionClass = LocalDateFormatter.class, args = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime transactionDate;
    public Double transactionAmount;
    @Trim
    public String transactionNarrative;
    @Trim
    public String transactionDescription;
    @Trim
    public String transactionId;
    @Trim
    public String transactionType;
    @Trim
    public String walletReference;
    public String dateOfTrans;


}
