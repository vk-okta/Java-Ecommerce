package com.ecommerce.javaecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;
    private String pg_id;
    private String pg_name;
    private String pg_status;
    private String pg_response_message;
}
