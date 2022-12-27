package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetLikedProductRes {
    private int productIdx;
    private String title;
    private String region;
    private int price;
    private String productImgUrl;
    private int likeCount;
}