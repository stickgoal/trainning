package me.maiz.project.forum26.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private int postId;

    private String title;

    private String content;

    private String author;

    private Date publishTime;

}
