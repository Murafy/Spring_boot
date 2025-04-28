package org.iclass.wos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PostLikeResponseDto {
    private Boolean success;
    private Boolean liked;
    private Integer count;
    private String message;
}