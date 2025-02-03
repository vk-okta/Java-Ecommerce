package com.ecommerce.javaecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    private AppRole roleName;

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}


/*
JPA will map the enum as an Integer using its ordinal value (i.e., its position in the declaration order).
So if not Enumerated type is not set to string

instead of -->

id	role_name
1	ADMIN
2	USER
3	SELLER

you will have -->

id	role_name
1	0
2	1
3	2

*/