package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.product.model.PostLikeReq;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.WITHDRAW_FAIL_USER;

@Service
public class ProductService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final ProductProvider productProvider;
    private final JwtService jwtService;


    @Autowired
    public ProductService(ProductDao productDao, ProductProvider productProvider, JwtService jwtService) {
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.jwtService = jwtService;

    }

    public PostProductRes createPost(int userIdx, PostProductReq postProductReq) throws BaseException{
        try{
            int productIdx = productDao.createPost(postProductReq, userIdx);
            for(int i=0; i<postProductReq.getProductImgUrl().size(); i++){
                productDao.createPostImgs(postProductReq.getProductImgUrl().get(i), productIdx);
            }
            return new PostProductRes(productIdx);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void likeProduct(PostLikeReq postLikeReq) throws BaseException {
        try {
            int result = productDao.likeProduct(postLikeReq);
            if (result == 0) {
                throw new BaseException(WITHDRAW_FAIL_USER);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
