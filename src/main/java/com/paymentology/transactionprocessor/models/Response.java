package com.paymentology.transactionprocessor.models;

import lombok.*;

@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class Response {
    private Integer totalRecords;
    private Integer matchingRecords;
    private Integer unmatchedRecords;
}
