
package com.example.omni_health_app.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsWithRoleResponseData {

    private boolean success;
    private List<UserDetailWithRole> userDetailWithRole;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
