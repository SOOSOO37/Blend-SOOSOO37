package com.blend.server.orderproduct;

import com.blend.server.product.Product;
import com.blend.server.global.audit.Auditable;
import com.blend.server.order.Order;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
@Setter
@Getter
@NoArgsConstructor
@Entity
public class OrderProduct extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderProductId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonManagedReference
    private Order order;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    @Column(nullable = false)
    private long quantity;

    private String trackingNumber;

    private OrderProductStatus orderProductStatus = OrderProductStatus.PAY_STANDBY;



    public enum OrderProductStatus{

        PAY_STANDBY(1,"결제대기"),

        PAY_FINISH(2,"결제완료"),

        DELIVERY_PROCESS(3, "배송준비중"),

        IN_DELIVERY(4, "배송중"),

        DELIVERED(5, "배송완료");

        @Getter
        private int number;

        @Getter
        private String description;

        OrderProductStatus(int number, String description){
            this.number = number;
            this.description = description;

        }
    }
}
