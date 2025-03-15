package com.ecommerce.javaecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @NotBlank
    @Size(min = 3, message = "Payment Method must contain at least 3 characters")
    private String paymentMethod;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Order order;

    // payment gateway details - like Stripe, Razorpay
    private String pg_id;
    private String pg_name;
    private String pg_status;
    private String pg_response_message;

    public Payment(String pgId, String paymentMethod, String pgName, String pgResponseMessage, String pgStatus) {
        this.pg_id = pgId;
        this.paymentMethod = paymentMethod;
        this.pg_name = pgName;
        this.pg_status = pgStatus;
        this.pg_response_message = pgResponseMessage;
    }
}
