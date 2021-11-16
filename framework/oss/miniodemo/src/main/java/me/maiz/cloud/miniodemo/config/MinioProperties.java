package me.maiz.cloud.miniodemo.config;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "file.store.minio"
)
public class MinioProperties {
    private static final Long DAY_MILLIS = 311040000L;
    private String endpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String tmpDir = "./tmp/";
    private Boolean tmpClean = true;
    private Date tmpFirstCleanTime;
    private Long tmpCleanPeriod;
    private Long tmpAliveDuration;

    public MinioProperties() {
        this.tmpCleanPeriod = DAY_MILLIS;
        this.tmpAliveDuration = DAY_MILLIS;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return this.bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Boolean getTmpClean() {
        return this.tmpClean;
    }

    public void setTmpClean(Boolean tmpClean) {
        this.tmpClean = tmpClean;
    }

    public String getTmpDir() {
        return this.tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public Date getTmpFirstCleanTime() {
        return this.tmpFirstCleanTime;
    }

    public void setTmpFirstCleanTime(String tmpFirstCleanTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;

        try {
            date = format.parse(tmpFirstCleanTime);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        this.tmpFirstCleanTime = date;
    }

    public Long getTmpCleanPeriod() {
        return this.tmpCleanPeriod;
    }

    public void setTmpCleanPeriod(Long tmpCleanPeriod) {
        this.tmpCleanPeriod = tmpCleanPeriod;
    }

    public Long getTmpAliveDuration() {
        return this.tmpAliveDuration;
    }

    public void setTmpAliveDuration(Long tmpAliveDuration) {
        this.tmpAliveDuration = tmpAliveDuration;
    }
}

