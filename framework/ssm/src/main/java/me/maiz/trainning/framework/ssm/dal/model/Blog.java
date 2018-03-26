package me.maiz.trainning.framework.ssm.dal.model;

public class Blog {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.BLOG_ID
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private Long blogId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.TITLE
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.STATE
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private String state;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.AUTHOR_NAME
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private String authorName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column BLOG.FEATURED
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    private Short featured;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column BLOG.BLOG_ID
     *
     * @return the value of BLOG.BLOG_ID
     *
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
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
     * @mbggenerated Mon Mar 26 15:01:05 CST 2018
     */
    public void setFeatured(Short featured) {
        this.featured = featured;
    }
}