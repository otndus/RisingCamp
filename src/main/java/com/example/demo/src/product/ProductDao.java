package com.example.demo.src.product;

import com.example.demo.src.product.model.*;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private List<GetProductImgRes> getProductImgRes;

    public int createPost(PostProductReq postProductReq, int userIdx){
        String createPostQuery = "insert into Product (title, productContents, userIdx, price, categoryIdx, priceProposal) VALUES (?,?,?,?,?,?)";
        Object[] createPostParams = new Object[]{postProductReq.getTitle(), postProductReq.getProductContents(), userIdx, postProductReq.getPrice(), postProductReq.getCategoryIdx(), postProductReq.getPriceProposal()};
        this.jdbcTemplate.update(createPostQuery, createPostParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int createPostImgs(PostProductImgReq postProductImgReq, int productIdx){
        String createPostImgsQuery = "insert into ProductImg (productIdx, productImgUrl) VALUES (?,?)";
        Object[] createPostImgsParams = new Object[]{productIdx, postProductImgReq.getProductImgUrl()};
        this.jdbcTemplate.update(createPostImgsQuery, createPostImgsParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int likeProduct(PostLikeReq postLikeReq){
        String likeProductQuery = "insert into ProductLike (productIdx, userIdx) VALUES (?,?)";
        Object[] likeProductParams = new Object[]{postLikeReq.getProductIdx(), postLikeReq.getUserIdx()};
        this.jdbcTemplate.update(likeProductQuery, likeProductParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public List<GetProductRes> getProduct(int productIdx){
        String getProductQuery = "select p.productIdx, title, productContents, nickName, region, mannerTemp, U.imgUrl, price, categoryName,\n" +
                "       priceProposal,\n" +
                "       if(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       case when timestampdiff(second, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "           when timestampdiff(minute, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "           when timestampdiff(hour, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "           when timestampdiff(day, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "           else timestampdiff(year, p.updatedAt, current_timestamp) end as updatedAt\n" +
                "    from Product p\n" +
                "inner join User U on p.userIdx = U.userIdx\n" +
                "inner join Category C on p.categoryIdx = C.categoryIdx\n" +
                "left join ProductImg PI on p.productIdx = PI.productIdx\n" +
                "left join(select productIdx, userIdx, count(userIdx) as likeCount from ProductLike where status = 'Y' group by productIdx) PL on p.productIdx = PL.productIdx\n" +
                "where p.productIdx = ?\n" +
                "group by p.productIdx";
        int getProductParams = productIdx;
        return this.jdbcTemplate.query(getProductQuery,
                (rs,rowNum) -> new GetProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("title"),
                        rs.getString("productContents"),
                        rs.getString("nickName"),
                        rs.getString("region"),
                        rs.getDouble("mannerTemp"),
                        rs.getString("imgUrl"),
                        rs.getInt("price"),
                        rs.getString("categoryName"),
                        rs.getString("priceProposal"),
                        rs.getInt("likeCount"),
                        rs.getString("updatedAt"),
                        getProductImgRes = this.jdbcTemplate.query("select productImgIdx, productImgUrl\n" +
                                "from ProductImg inner join Product P on ProductImg.productIdx = P.productIdx\n" +
                                "where ProductImg.status = 'Y' and P.productIdx = ?",
                                (rk,rownum) -> new GetProductImgRes(
                                        rk.getInt("productImgIdx"),
                                        rk.getString("productImgUrl")
                                ), productIdx)),
                getProductParams);
    }

    public List<GetLikedProductRes> getLikedProducts(int userIdx){
        String getLikedProductQuery = "select p.productIdx, title, region, price, productImgUrl,\n" +
                "       if(likeCount is null, 0, likeCount) as likeCount\n" +
                "    from Product p\n" +
                "inner join User U on p.userIdx = U.userIdx\n" +
                "left join ProductImg PI on p.productIdx = PI.productIdx\n" +
                "inner join(select productIdx, userIdx, count(userIdx) as likeCount from ProductLike where status = 'Y' group by productIdx) PL on p.productIdx = PL.productIdx\n" +
                "where p.userIdx = ?\n" +
                "group by p.productIdx";
        int getLikedProductParams = userIdx;
        return this.jdbcTemplate.query(getLikedProductQuery,
                (rs,rowNum) -> new GetLikedProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("title"),
                        rs.getString("region"),
                        rs.getInt("price"),
                        rs.getString("productImgUrl"),
                        rs.getInt("likeCount")),
                getLikedProductParams);
    }

    public List<GetCategoryProductRes> getCategoryProducts(int categoryIdx){
        String getCategoryProductQuery = "select p.productIdx, title, region, price, productImgUrl,\n" +
                "       if(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       case when timestampdiff(second, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "           when timestampdiff(minute, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "           when timestampdiff(hour, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "           when timestampdiff(day, p.updatedAt, current_timestamp) < 60 then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "           else timestampdiff(year, p.updatedAt, current_timestamp) end as updatedAt\n" +
                "    from Product p\n" +
                "inner join User U on p.userIdx = U.userIdx\n" +
                "inner join Category C on p.categoryIdx = C.categoryIdx\n" +
                "left join ProductImg PI on p.productIdx = PI.productIdx\n" +
                "left join(select productIdx, userIdx, count(userIdx) as likeCount from ProductLike where status = 'Y' group by productIdx) PL on p.productIdx = PL.productIdx\n" +
                "where C.categoryIdx = ?\n" +
                "group by p.productIdx";
        int getCategoryProductParams = categoryIdx;
        return this.jdbcTemplate.query(getCategoryProductQuery,
                (rs,rowNum) -> new GetCategoryProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("title"),
                        rs.getString("region"),
                        rs.getInt("price"),
                        rs.getString("productImgUrl"),
                        rs.getInt("likeCount"),
                        rs.getString("updatedAt")),
                getCategoryProductParams);
    }
}
