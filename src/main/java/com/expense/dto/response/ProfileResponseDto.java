package com.expense.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDto {

    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private String phone;
    private String address;
    private String city;
    private String picture;
}