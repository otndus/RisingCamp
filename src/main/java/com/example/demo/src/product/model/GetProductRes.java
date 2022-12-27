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
public class GetProductRes {
    private int productIdx;
    private String title;
    private String productContents;
    private String nickName;
    private String region;
    private Double mannerTemp;
    private String imgUrl;
    private int price;
    private String categoryName;
    private String priceProposal;
    private int likeCount;
    private String updatedAt;
    private List<GetProductImgRes> productImg;
}