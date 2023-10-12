package com.blend.server.seller;

import com.blend.server.Product.Product;
import com.blend.server.global.audit.Auditable;
import com.blend.server.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.security.Principal;
import java.util.List;

@Setter
@Getter
@Entity
public class Seller extends Auditable implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,updatable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 10)
    private String regNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 12)
    private String phone;

    @Column(nullable = false, length = 14)
    private String accountNumber;

    @Column(nullable = false)
    private String bank;

    @Column
    @Enumerated(value = EnumType.STRING)
    private SellerStatus sellerStatus = SellerStatus.SELLER_WAIT;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.PERSIST)
    private User user;

    @OneToMany(mappedBy = "seller", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Product> product;

    public enum SellerStatus {

        SELLER_WAIT(1, "가입 대기"),
        SELLER_APPROVE(2, "가입 승인"),
        SELLER_REJECTED(3, "가입 거절");

        @Getter
        private int number;

        @Getter
        private String description;

        SellerStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }
}
