package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.ProductProvider;
import com.example.demo.src.product.ProductService;
import com.example.demo.src.product.model.*;
import com.example.demo.src.user.model.DeleteUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;

    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService){
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/{userIdx}")
    public BaseResponse<PostProductRes> createPost(@PathVariable("userIdx") int userIdx, @RequestBody PostProductReq postProductReq){
        if(postProductReq.getTitle() == null || postProductReq.getProductContents() == null || postProductReq.getCategoryIdx() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCT_EMPTY);
        }
        if(postProductReq.getProductImgUrl().size() > 10){
            return new BaseResponse<>(BaseResponseStatus.POST_PRODUCTIMG_SIZE);
        }
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostProductRes postProductRes = productService.createPost(userIdx, postProductReq);
            return new BaseResponse<>(postProductRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/like") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> withdrawUser(@RequestBody PostLikeReq postLikeReq){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(postLikeReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.likeProduct(postLikeReq);

            String result = "관심 목록에 저장되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/like/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<List<GetLikedProductRes>> likedProducts(@PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            return new BaseResponse<>(this.productProvider.getLikedProducts(userIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/category/{categoryIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<List<GetCategoryProductRes>> categoryProducts(@PathVariable("categoryIdx") int categoryIdx){
        try{
            return new BaseResponse<>(this.productProvider.getCategoryProduct(categoryIdx));
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{productIdx}")
    public BaseResponse<List<GetProductRes>> getPost(@PathVariable("productIdx") int productIdx){
        try{
            return new BaseResponse<>(this.productProvider.getProduct(productIdx));
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}




