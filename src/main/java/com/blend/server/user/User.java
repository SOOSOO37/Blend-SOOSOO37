package com.blend.server.user;

import com.blend.server.cart.Cart;
import com.blend.server.global.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.security.auth.Subject;
import javax.validation.constraints.Email;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
@Getter
public class User extends Auditable implements Principal {

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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL )
    private Cart cart;

    @Override
    public String getName() {
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
}
