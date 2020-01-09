package me.maiz.project.forum26.controller;

import me.maiz.project.forum26.common.Result;
import me.maiz.project.forum26.entity.Post;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("post")
@CrossOrigin("*") //解决跨域问题
public class PostController {
    //伪造数据
    private static final  List<Post> posts = new ArrayList<>();
       static {
           posts.add(new Post(1,"MBP拆机","今天我们来拆解MBP2019","macfuns",new Date()));
           posts.add(new Post(2,"huawei新机鉴赏1","我们一起来鉴赏华为的P30","一线新机",new Date()));
           posts.add(new Post(3,"huawei新机鉴赏2","我们一起来鉴赏华为的P30","一线新机",new Date()));
           posts.add(new Post(4,"huawei新机鉴赏3","我们一起来鉴赏华为的P30","一线新机",new Date()));
           posts.add(new Post(5,"huawei新机鉴赏4","我们一起来鉴赏华为的P30","一线新机",new Date()));
           posts.add(new Post(6,"huawei新机鉴赏5","我们一起来鉴赏华为的P30","一线新机",new Date()));
       }



    @RequestMapping("getAll")
    public Result getAllPosts(@RequestParam(required = false,defaultValue = "1")int pageIdx,@RequestParam(required = false,defaultValue = "10") int size){
        return Result.success(posts);
    }

    @RequestMapping("detail")
    public Result detail(int id){
        List<Post> collect = posts.stream().filter(p -> p.getPostId() == id).collect(Collectors.toList());

        return Result.success(collect.get(0));
    }

}
