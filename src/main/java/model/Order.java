package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enums.OrderStatus;
import model.enums.PaymentMethod;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private String id;
    private User user;
    private Product product;
    private Date date;
    private double price;
    private int quantity;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
}
