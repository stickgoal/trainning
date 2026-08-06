package com.yourproject.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试数据生成器
 * 生成多格式测试文档和 QA 问答对
 * 支持网络抓取（Jsoup）和本地生成两种模式
 */
@Component
public class TestDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(TestDataGenerator.class);

    @Value("${test.test-docs-path}")
    private String testDocsPath;

    @Value("${test.evaluation-output-path}")
    private String outputPath;

    /**
     * QA 问答对
     */
    public record QAPair(String question, String expectedAnswer, String sourceFile, String keywords) {}

    /**
     * 生成测试数据集
     * 1. 生成多格式测试文档
     * 2. 从文档中抽取 QA 问答对
     */
    public List<QAPair> generateTestDataset() {
        log.info("开始生成测试数据集...");

        // 确保目录存在
        createDirs();

        // 1. 生成多格式测试文档
        generateMarkdownDocs();
        generateTxtDocs();
        generateHtmlDocs();
        generateFaqJson();

        // 2. 从文档内容构建 QA 问答对
        List<QAPair> qaPairs = buildQAPairs();

        log.info("测试数据集生成完成: {} 个 QA 对", qaPairs.size());
        return qaPairs;
    }

    /**
     * 构建 QA 问答对（基于生成的测试文档内容）
     */
    private List<QAPair> buildQAPairs() {
        List<QAPair> pairs = new ArrayList<>();

        // 人力资源类问答
        pairs.add(new QAPair(
                "公司年假是多少天？",
                "入职满一年享有5天年假，满三年10天，满十年15天。",
                "hr_policy.md",
                "年假,年休假,假期"
        ));
        pairs.add(new QAPair(
                "试用期多长时间？",
                "试用期3个月，表现优秀可申请缩短至2个月。",
                "hr_policy.md",
                "试用期,试用期长度"
        ));
        pairs.add(new QAPair(
                "迟到扣款标准是什么？",
                "迟到15分钟以内扣50元，15-30分钟扣100元，超过30分钟按旷工半天处理。",
                "hr_policy.md",
                "迟到,扣款,考勤"
        ));
        pairs.add(new QAPair(
                "公司提供哪些保险？",
                "五险一金：养老、医疗、失业、工伤、生育保险及住房公积金。",
                "hr_policy.md",
                "保险,五险一金,社保"
        ));
        pairs.add(new QAPair(
                "加班费怎么计算？",
                "工作日加班1.5倍工资，周末加班2倍工资，法定节假日3倍工资。",
                "hr_policy.md",
                "加班费,加班,加班工资"
        ));

        // 财务类问答
        pairs.add(new QAPair(
                "报销流程是什么？",
                "填写报销单→部门主管审批→财务审核→出纳付款，一般3-5个工作日到账。",
                "finance_manual.md",
                "报销,报销流程,报销单"
        ));
        pairs.add(new QAPair(
                "差旅费标准是多少？",
                "一线城市住宿500元/天，二线400元/天，交通费实报实销。",
                "finance_manual.md",
                "差旅费,出差,差旅标准"
        ));
        pairs.add(new QAPair(
                "发票抬头是什么？",
                "公司名称：示例科技有限公司，税号：91110100MA01XXYYZZ。",
                "finance_manual.md",
                "发票,发票抬头,税号"
        ));
        pairs.add(new QAPair(
                "备用金申请条件？",
                "出差天数超过3天或预计费用超过2000元可申请备用金。",
                "finance_manual.md",
                "备用金,预借款,出差借款"
        ));
        pairs.add(new QAPair(
                "采购审批权限怎么划分？",
                "5000元以下部门主管审批，5000-50000元总监审批，50000元以上VP审批。",
                "finance_manual.md",
                "采购,审批,采购权限"
        ));

        // 技术文档类问答
        pairs.add(new QAPair(
                "API网关支持哪些认证方式？",
                "支持JWT、OAuth2.0、API Key三种认证方式。",
                "tech_docs.md",
                "API网关,认证,JWT,OAuth"
        ));
        pairs.add(new QAPair(
                "系统最大并发量是多少？",
                "单实例支持5000并发，集群模式可扩展至50000并发。",
                "tech_docs.md",
                "并发,并发量,性能"
        ));
        pairs.add(new QAPair(
                "数据备份策略是什么？",
                "每日增量备份，每周全量备份，保留30天，异地容灾备份。",
                "tech_docs.md",
                "备份,数据备份,备份策略"
        ));
        pairs.add(new QAPair(
                "日志保留多长时间？",
                "应用日志保留90天，访问日志保留180天，审计日志保留3年。",
                "tech_docs.md",
                "日志,日志保留,日志策略"
        ));
        pairs.add(new QAPair(
                "微服务间通信方式是什么？",
                "同步通信使用gRPC，异步通信使用RabbitMQ消息队列。",
                "tech_docs.md",
                "微服务,通信,gRPC,RabbitMQ"
        ));

        // FAQ 类问答
        pairs.add(new QAPair(
                "如何重置密码？",
                "在登录页面点击'忘记密码'，输入注册邮箱，通过邮件链接重置。",
                "faq.json",
                "密码,重置密码,忘记密码"
        ));
        pairs.add(new QAPair(
                "支持哪些浏览器？",
                "Chrome 90+、Firefox 88+、Edge 90+、Safari 14+。",
                "faq.json",
                "浏览器,兼容性,支持"
        ));
        pairs.add(new QAPair(
                "数据导出格式有哪些？",
                "支持CSV、Excel、JSON、PDF四种导出格式。",
                "faq.json",
                "导出,数据导出,导出格式"
        ));
        pairs.add(new QAPair(
                "系统维护时间是什么时候？",
                "每周日凌晨2:00-4:00为定期维护窗口。",
                "faq.json",
                "维护,维护时间,系统维护"
        ));
        pairs.add(new QAPair(
                "如何申请权限？",
                "在OA系统提交权限申请单，直属主管审批后IT部门开通。",
                "faq.json",
                "权限,申请权限,权限申请"
        ));

        // 补充更多问答对
        pairs.add(new QAPair(
                "病假工资怎么算？",
                "病假期间工资按基本工资的80%发放，需提供医院证明。",
                "hr_policy.md",
                "病假,病假工资,请假"
        ));
        pairs.add(new QAPair(
                "年终奖发放标准是什么？",
                "年终奖根据公司效益和个人绩效，发放1-3个月工资。",
                "hr_policy.md",
                "年终奖,奖金,年终奖标准"
        ));
        pairs.add(new QAPair(
                "调岗流程是什么？",
                "员工提交调岗申请→现部门主管同意→目标部门接收→HR审批。",
                "hr_policy.md",
                "调岗,调岗流程,岗位调动"
        ));
        pairs.add(new QAPair(
                "公积金缴存比例是多少？",
                "公司和个人各缴存12%，按月工资基数计算。",
                "hr_policy.md",
                "公积金,缴存比例,住房公积金"
        ));
        pairs.add(new QAPair(
                "年会的举办时间是什么时候？",
                "公司年会通常在每年1月中旬举办，具体时间另行通知。",
                "hr_policy.md",
                "年会,年会时间,公司活动"
        ));
        pairs.add(new QAPair(
                "预算审批需要多久？",
                "年度预算审批需15个工作日，季度调整需5个工作日。",
                "finance_manual.md",
                "预算,预算审批,审批周期"
        ));
        pairs.add(new QAPair(
                "合同付款方式有哪些？",
                "支持银行转账、商业汇票、电子支付三种方式。",
                "finance_manual.md",
                "付款,付款方式,合同付款"
        ));
        pairs.add(new QAPair(
                "固定资产折旧年限？",
                "电子设备3年，办公家具5年，车辆8年，建筑物40年。",
                "finance_manual.md",
                "折旧,固定资产,折旧年限"
        ));
        pairs.add(new QAPair(
                "系统监控告警阈值是多少？",
                "CPU>80%持续5分钟告警，内存>85%持续5分钟告警，磁盘>90%立即告警。",
                "tech_docs.md",
                "监控,告警,告警阈值"
        ));
        pairs.add(new QAPair(
                "CDN加速域名配置在哪里？",
                "在控制台→CDN管理→域名配置中添加加速域名。",
                "tech_docs.md",
                "CDN,域名配置,加速"
        ));

        return pairs;
    }

    private void generateMarkdownDocs() {
        // 人力资源制度文档
        String hrPolicy = """
                # 人力资源管理制度

                ## 1. 考勤管理

                ### 1.1 工作时间
                公司实行标准工时制，工作时间为周一至周五 9:00-18:00，午休1小时。

                ### 1.2 试用期
                新员工试用期3个月，表现优秀可申请缩短至2个月。试用期内享有正式员工同等福利。

                ### 1.3 迟到与早退
                - 迟到15分钟以内：扣款50元
                - 迟到15-30分钟：扣款100元
                - 迟到超过30分钟：按旷工半天处理
                - 早退参照迟到标准执行

                ## 2. 假期管理

                ### 2.1 年假
                - 入职满一年：5天年假
                - 入职满三年：10天年假
                - 入职满十年：15天年假

                ### 2.2 病假
                病假期间工资按基本工资的80%发放，需提供二级以上医院证明。全年病假累计不超过30天。

                ### 2.3 婚假
                法定婚假3天，晚婚增加7天，共计10天。

                ## 3. 薪酬福利

                ### 3.1 五险一金
                公司为正式员工缴纳五险一金：
                - 养老保险
                - 医疗保险
                - 失业保险
                - 工伤保险
                - 生育保险
                - 住房公积金（公司和个人各缴存12%）

                ### 3.2 加班费
                - 工作日加班：1.5倍工资
                - 周末加班：2倍工资
                - 法定节假日加班：3倍工资

                ### 3.3 年终奖
                年终奖根据公司效益和个人绩效考核结果发放，标准为1-3个月工资。

                ## 4. 岗位变动

                ### 4.1 调岗流程
                员工提交调岗申请→现部门主管同意→目标部门接收→HR审批→生效。
                
                ### 4.2 晋升周期
                每年4月和10月进行两次晋升评估。
                """;
        writeFile("hr_policy.md", hrPolicy);

        // 财务操作手册
        String financeManual = """
                # 财务操作手册

                ## 1. 报销管理

                ### 1.1 报销流程
                1. 填写报销单（附原始发票）
                2. 部门主管审批
                3. 财务部门审核
                4. 出纳付款
                整个流程一般3-5个工作日完成。

                ### 1.2 发票要求
                - 发票抬头：示例科技有限公司
                - 税号：91110100MA01XXYYZZ
                - 开票日期与消费日期一致
                - 金额合计需与报销金额一致

                ## 2. 差旅费管理

                ### 2.1 差旅标准
                - 一线城市（北上广深）：住宿500元/天，餐补100元/天
                - 二线城市：住宿400元/天，餐补80元/天
                - 三线及以下城市：住宿300元/天，餐补60元/天
                - 城市间交通费实报实销

                ### 2.2 备用金
                出差天数超过3天或预计费用超过2000元可申请备用金。
                备用金需在出差结束后5个工作日内核销。

                ## 3. 采购管理

                ### 3.1 审批权限
                - 5000元以下：部门主管审批
                - 5000-50000元：总监审批
                - 50000元以上：VP审批

                ### 3.2 合同付款方式
                - 银行转账
                - 商业汇票
                - 电子支付

                ## 4. 固定资产管理

                ### 4.1 折旧年限
                - 电子设备：3年
                - 办公家具：5年
                - 车辆：8年
                - 建筑物：40年

                ## 5. 预算管理

                ### 5.1 预算审批周期
                - 年度预算：需15个工作日
                - 季度调整：需5个工作日
                - 临时预算：需3个工作日
                """;
        writeFile("finance_manual.md", financeManual);

        // 技术文档
        String techDocs = """
                # 系统技术文档

                ## 1. 系统架构

                ### 1.1 整体架构
                系统采用微服务架构，通过API网关统一入口。
                API网关支持JWT、OAuth2.0、API Key三种认证方式。

                ### 1.2 微服务通信
                - 同步通信：gRPC
                - 异步通信：RabbitMQ消息队列

                ## 2. 性能指标

                ### 2.1 并发能力
                - 单实例：支持5000并发
                - 集群模式：可扩展至50000并发
                - 响应时间：P99 < 200ms

                ## 3. 运维管理

                ### 3.1 数据备份
                - 每日增量备份
                - 每周全量备份
                - 保留30天
                - 异地容灾备份

                ### 3.2 日志管理
                - 应用日志：保留90天
                - 访问日志：保留180天
                - 审计日志：保留3年

                ### 3.3 系统监控
                - CPU > 80% 持续5分钟告警
                - 内存 > 85% 持续5分钟告警
                - 磁盘 > 90% 立即告警
                - 接口错误率 > 1% 持续1分钟告警

                ### 3.4 定期维护
                每周日凌晨2:00-4:00为定期维护窗口。

                ## 4. CDN配置

                CDN加速域名在控制台→CDN管理→域名配置中添加。
                支持HTTPS证书自动部署。
                """;
        writeFile("tech_docs.md", techDocs);
    }

    private void generateTxtDocs() {
        String txtContent = """
                常见问题解答（FAQ）

                Q: 如何重置密码？
                A: 在登录页面点击"忘记密码"，输入注册邮箱，通过邮件链接重置密码。

                Q: 支持哪些浏览器？
                A: Chrome 90+、Firefox 88+、Edge 90+、Safari 14+。

                Q: 数据导出格式有哪些？
                A: 支持CSV、Excel、JSON、PDF四种导出格式。

                Q: 系统维护时间是什么时候？
                A: 每周日凌晨2:00-4:00为定期维护窗口。

                Q: 如何申请权限？
                A: 在OA系统提交权限申请单，直属主管审批后IT部门开通。

                Q: 新员工入职需要准备什么材料？
                A: 身份证复印件、学历证明、离职证明、体检报告、银行卡信息。

                Q: 忘记工号怎么办？
                A: 联系HR部门查询，或通过企业微信→我→个人信息查看工号。
                """;
        writeFile("faq.txt", txtContent);
    }

    private void generateHtmlDocs() {
        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head><title>公司规章制度</title></head>
                <body>
                <h1>公司规章制度</h1>
                <h2>第一章 总则</h2>
                <p>本制度适用于公司全体正式员工，自发布之日起生效。</p>
                <h2>第二章 考勤制度</h2>
                <p>工作时间：周一至周五 9:00-18:00，午休1小时。</p>
                <p>迟到15分钟以内扣50元，15-30分钟扣100元，超过30分钟按旷工半天处理。</p>
                <h2>第三章 假期制度</h2>
                <p>年假：满一年5天，满三年10天，满十年15天。</p>
                <p>病假按基本工资80%发放，需医院证明。</p>
                <h2>第四章 薪酬福利</h2>
                <p>五险一金：养老、医疗、失业、工伤、生育保险及住房公积金。</p>
                <p>公积金缴存比例：公司和个人各12%。</p>
                <p>加班费：工作日1.5倍，周末2倍，法定节假日3倍。</p>
                <p>年终奖：1-3个月工资，根据绩效发放。</p>
                </body>
                </html>
                """;
        writeFile("company_rules.html", htmlContent);
    }

    private void generateFaqJson() {
        String faqJson = """
                [
                  {"q": "如何重置密码？", "a": "在登录页面点击'忘记密码'，输入注册邮箱，通过邮件链接重置。"},
                  {"q": "支持哪些浏览器？", "a": "Chrome 90+、Firefox 88+、Edge 90+、Safari 14+。"},
                  {"q": "数据导出格式有哪些？", "a": "支持CSV、Excel、JSON、PDF四种导出格式。"},
                  {"q": "系统维护时间是什么时候？", "a": "每周日凌晨2:00-4:00为定期维护窗口。"},
                  {"q": "如何申请权限？", "a": "在OA系统提交权限申请单，直属主管审批后IT部门开通。"},
                  {"q": "新员工入职需要准备什么材料？", "a": "身份证复印件、学历证明、离职证明、体检报告、银行卡信息。"},
                  {"q": "忘記工號怎麼辦？", "a": "联系HR部门查询，或通过企业微信查看个人信息。"},
                  {"q": "公司年会什么时候举办？", "a": "通常在每年1月中旬举办，具体时间另行通知。"},
                  {"q": "调岗流程是什么？", "a": "提交调岗申请→现部门主管同意→目标部门接收→HR审批。"},
                  {"q": "合同付款方式有哪些？", "a": "银行转账、商业汇票、电子支付三种方式。"},
                  {"q": "预算审批需要多久？", "a": "年度预算15个工作日，季度调整5个工作日。"},
                  {"q": "采购审批权限怎么划分？", "a": "5000元以下部门主管审批，5000-50000元总监审批，50000元以上VP审批。"},
                  {"q": "系统监控告警阈值是多少？", "a": "CPU>80%持续5分钟告警，内存>85%持续5分钟告警，磁盘>90%立即告警。"},
                  {"q": "数据备份策略是什么？", "a": "每日增量备份，每周全量备份，保留30天，异地容灾。"},
                  {"q": "微服务间通信方式是什么？", "a": "同步使用gRPC，异步使用RabbitMQ。"},
                  {"q": "API网关支持哪些认证方式？", "a": "JWT、OAuth2.0、API Key三种。"},
                  {"q": "系统最大并发量是多少？", "a": "单实例5000并发，集群50000并发。"},
                  {"q": "CDN加速域名配置在哪里？", "a": "控制台→CDN管理→域名配置中添加。"},
                  {"q": "日志保留多长时间？", "a": "应用日志90天，访问日志180天，审计日志3年。"},
                  {"q": "固定资产折旧年限？", "a": "电子设备3年，办公家具5年，车辆8年，建筑物40年。"}
                ]
                """;
        writeFile("faq.json", faqJson);
    }

    private void createDirs() {
        try {
            Files.createDirectories(Path.of(testDocsPath));
            Files.createDirectories(Path.of(outputPath));
        } catch (IOException e) {
            log.error("创建目录失败", e);
        }
    }

    private void writeFile(String filename, String content) {
        try {
            Path path = Path.of(testDocsPath, filename);
            Files.writeString(path, content);
            log.info("生成测试文档: {}", path);
        } catch (IOException e) {
            log.error("写入文件失败: {}", filename, e);
        }
    }
}
