package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUserRes = userDao.getUsersByIdx(userIdx);
            return getUserRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkPhone(String phone) throws BaseException{
        try{
            return userDao.checkUser(phone);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }





}
