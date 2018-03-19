package me.maiz.trainning.framework.mybatis.dal.mapper;

import me.maiz.trainning.framework.mybatis.dal.model.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Lucas on 2018-03-19.
 */
public interface BlogMapper {

    List<Blog> findActiveBlogWithTitleLike(@Param("title") String title);

    List<Blog> findActiveBlogLike(@Param("title") String title ,@Param("authorName") String authorName);

    List<Blog> findActiveBlogLikeOr(@Param("title") String title ,@Param("authorName") String authorName);

    List<Blog> findActiveBlogLikeWhere(@Param("title") String title ,@Param("authorName") String authorName);

    List<Blog> findActiveBlogLikeWhere2(@Param("title") String title ,@Param("authorName") String authorName);

    void updateBlogIfNecessary(Blog blog);

}
