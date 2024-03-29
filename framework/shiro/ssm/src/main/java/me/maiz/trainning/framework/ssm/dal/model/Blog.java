package me.maiz.trainning.framework.ssm.dal.model;

import java.io.Serializable;

public class Blog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.BLOG_ID
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private Long blogId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.TITLE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.STATE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private String state;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.AUTHOR_NAME
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private String authorName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.FEATURED
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private Short featured;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table BLOG
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.BLOG_ID
     *
     * @return the value of BLOG.BLOG_ID
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public Long getBlogId() {
        return blogId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BLOG.BLOG_ID
     *
     * @param blogId the value for BLOG.BLOG_ID
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.TITLE
     *
     * @return the value of BLOG.TITLE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BLOG.TITLE
     *
     * @param title the value for BLOG.TITLE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.STATE
     *
     * @return the value of BLOG.STATE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public String getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BLOG.STATE
     *
     * @param state the value for BLOG.STATE
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.AUTHOR_NAME
     *
     * @return the value of BLOG.AUTHOR_NAME
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BLOG.AUTHOR_NAME
     *
     * @param authorName the value for BLOG.AUTHOR_NAME
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.FEATURED
     *
     * @return the value of BLOG.FEATURED
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public Short getFeatured() {
        return featured;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column BLOG.FEATURED
     *
     * @param featured the value for BLOG.FEATURED
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    public void setFeatured(Short featured) {
        this.featured = featured;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table BLOG
     *
     * @mbg.generated Wed Mar 28 15:11:04 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", blogId=").append(blogId);
        sb.append(", title=").append(title);
        sb.append(", state=").append(state);
        sb.append(", authorName=").append(authorName);
        sb.append(", featured=").append(featured);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}