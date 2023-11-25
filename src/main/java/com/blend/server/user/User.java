package com.blend.server.user;

import com.blend.server.admin.Admin;
import com.blend.server.cart.Cart;
import com.blend.server.global.audit.Auditable;
import com.blend.server.order.Order;
import com.blend.server.review.Review;
import com.blend.server.seller.Seller;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.security.Principal;
import java.util.*;


@NoArgsConstructor
@Setter
@Entity
@Getter
public class User extends Auditable implements Principal{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @BatchSize(size = 10)
    @Fetch(value = FetchMode.SUBSELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Order> orderList;

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Review> reviews;

    public User(String username, String s, List<GrantedAuthority> authorities) {
        super();
    }

    @Override
    public String getName() {
        return null;
    }

    public Map.Entry<Object, Object> getRole() {
        return null;
    }

    public enum UserStatus {
        ACTIVE(1, "활동중"),

        SLEEP(2,"휴먼계정"),

        QUIT(3,"탈퇴");


        @Getter
        private int number;

        @Getter
        private String description;

        UserStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }
    public User(Cart cart) {
        this.cart = cart;
    }

    public void addReview(Review review) {
        if(this.reviews == null){
           this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        review.setUser(this);
    }
}
