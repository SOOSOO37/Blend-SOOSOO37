package com.blend.server.order;

import com.blend.server.global.audit.Auditable;
import com.blend.server.orderproduct.OrderProduct;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Entity(name = "ORDERS")
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private int phoneNumber;

    @Column(nullable = false)
    private String payMethod;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.ORDER_DONE;

    @JsonBackReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderProduct> orderProductList = new ArrayList<>();

    public enum OrderStatus {

        ORDER_DONE(1, "주문완료"),

        ORDER_CANCEL(2, "주문취소");


        @Getter
        private int number;

        @Getter
        private String description;

        OrderStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }
}
