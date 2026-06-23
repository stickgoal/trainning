package me.maiz.langchain4jdemo.agentic.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CvController {
    @Autowired
    private CvGenerator cvGenerator;


    @GetMapping("/cv")
    public String cv(@RequestParam(defaultValue = "") String lifeStory) {
        return cvGenerator.generateCv("""
我叫林泽宇，今年26岁，现在住在杭州余杭，想找一份Java后端开发的工作，期望薪资18到22k，一个月之后能入职。手机号138-XXXX-7890，邮箱linzeyu_dev@163.com。
我2017年九月到2021年六月在浙江理工大学读本科，专业是计算机科学与技术，绩点3.2满分4，学过数据结构、操作系统、计算机网络、Java、数据库还有分布式相关课程。大学拿过校程序设计竞赛二等奖，还有一次三等奖学金，高数当时挂过一次，后面补考才过。
毕业之后2021年7月先去了杭州星软信息，一开始是实习Java开发，后来转正式初级开发，做到23年2月。这家公司做企业OA系统，平时用SSM框架写考勤、审批、组织架构相关功能，用MyBatis做复杂多表查询，偶尔写存储过程，对接过短信和OSS文件存储第三方接口，用Quartz定时任务自动生成每月考勤报表。
23年3月跳槽到杭州云途科技，一直做到现在还在职，公司是做电商SaaS，给线下中小商家服务。日常主要用SpringBoot、SpringCloud写订单、支付、库存核心接口，做过MySQL分库分表，搭配Redis缓存解决并发下单超卖，依靠RocketMQ异步处理订单消息解耦，线上经常排查慢SQL、FullGC、接口超时这类故障，写完业务会补单元测试，写接口文档跟前端、测试对接。
手上主要做的线上项目是商户订单中台，从23年4月开发维护到现在，技术用Java8和17混合、SpringCloud Alibaba、Nacos、MyBatis-Plus、MySQL8、Redis、RocketMQ、Seata、ES、Docker，平台每天峰值能处理十二万多订单。我全权负责订单创建、支付回调、售后退款整套流程，引入Seata分布式事务保证订单、库存、支付数据一致。之前订单查询页面加载很慢，我接入ES做商品订单检索，分页查询响应从八百多毫秒降到一百毫秒以内。库存模块原来并发下经常超卖，我用Redis预扣库存+消息队列最终扣减的方案，线上超卖问题直接清零。还搭建了简单的定时任务监控，告警异常订单，每周自动统计商户经营数据报表。
另外大学毕业设计做过简易图书商城单体项目，SSM加MySQL，实现图书上架、购物车、下单功能；业余在gitee写过一个小型分布式任务调度开源demo，基于SpringBoot+Redis延时队列，有几百个小星星。
平时熟练掌握Java基础、并发、JVM调优，熟悉Spring全家桶、SpringCloud Alibaba微服务整套组件，数据库会索引优化、分库分表，中间件Redis、RocketMQ、Elasticsearch都有线上落地经验，了解Seata分布式事务，会Docker简单打包部署，能看懂基础Linux命令排查服务器问题，了解Git版本管理。
工作三年，平时习惯梳理业务文档，遇到线上故障会复盘记录优化方案，能独立承接需求从方案设计到上线全流程，沟通对接产品、测试都没问题。
                """);
    }
}
