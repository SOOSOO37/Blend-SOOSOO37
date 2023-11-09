package com.blend.server.admin;

import com.blend.server.global.audit.Auditable;
import com.blend.server.user.User;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends Auditable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String password;
    @OneToOne(mappedBy = "admin")
    private User user;

    @Builder
    public Admin(Long id, String email, String password, User user) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.user = user;
    }
}
