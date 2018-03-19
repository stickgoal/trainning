package me.maiz.trainning.framework.mybatis.dal.model;

/**
 * Created by Lucas on 2018-03-19.
 */
public class Blog {

    private int blogId;

    /**
     * 状态
     */
    private String state;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 特点
     */
    private String featured;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Blog{");
        sb.append("state='").append(state).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", authorName='").append(authorName).append('\'');
        sb.append(", featured='").append(featured).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }
}
