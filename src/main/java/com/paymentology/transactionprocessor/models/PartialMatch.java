package com.paymentology.transactionprocessor.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class PartialMatch {
    private Transaction transaction;
    private Transaction transaction2;
    private List<String> fields = new ArrayList<>();
}
