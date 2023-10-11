package com.blend.server.user;

import com.blend.server.global.audit.Auditable;
import lombok.Getter;

@Getter
public class UserResponseDto extends Auditable {

    private long id;

    private String email;

    private String nickName;

    private String address;

    private String userStatus;

}
