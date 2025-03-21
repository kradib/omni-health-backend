package com.example.omni_health_app.dto.response;

import lombok.Builder;


public class UserDetailsWithRoleResponse extends ResponseWrapper<UserDetailsWithRoleResponseData> {

     @Builder
     public UserDetailsWithRoleResponse(UserDetailsWithRoleResponseData data, ResponseMetadata responseMetadata) {
          super(data, responseMetadata);
     }

}
