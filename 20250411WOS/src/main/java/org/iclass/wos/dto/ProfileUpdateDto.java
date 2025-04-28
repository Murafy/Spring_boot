package org.iclass.wos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProfileUpdateDto {
    private String id;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private String address;
    private String phone;
}