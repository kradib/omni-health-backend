package com.example.omni_health_app.domain.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    private String roles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_detail_id", referencedColumnName = "id")
    @JsonManagedReference
    private UserDetail userDetail;

    public static class UserAuthBuilder {
        public UserAuthBuilder roles(String roles) {
            this.roles = roles;
            return this;
        }
    }

}
