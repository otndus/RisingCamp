package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryProductRes {
    private int productIdx;
    private String title;
    private String region;
    private int price;
    private String productImgUrl;
    private int likeCount;
    private String updatedAt;
}