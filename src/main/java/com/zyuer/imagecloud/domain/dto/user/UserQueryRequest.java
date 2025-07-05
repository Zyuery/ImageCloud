package com.zyuer.imagecloud.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import com.zyuer.imagecloud.domain.dto.normal.PageRequest;

//@EqualsAndHashCode(callSuper = true)注解表示自动生成equals()和hashCode()方法时，生成的代码会包含父类字段的比对
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userName;
    private String userAccount;
    private String userProfile;
    private String userRole;
}
