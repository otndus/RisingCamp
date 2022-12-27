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
public class PostProductReq {
    private String title;
    private String productContents;
    private int price;
    private Long categoryIdx;
    private String priceProposal;
    private List<PostProductImgReq> productImgUrl;
}