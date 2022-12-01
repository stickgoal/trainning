package me.maiz.project.highconcurrency.service;

public interface RedisService<V> {
    /**
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，以秒为单位
     */
    void set(String key,String value,long expireTime);

    /**
     *
     * @param key
     * @param value
     * @param expireDays 过期天数，以日为单位
     */
    void setExpireDays(String key,String value,int expireDays);


    /**
     *
     * @param key
     * @param value
     * @param expireMinutes 过期分钟数，以分钟为单位
     */
    void setExpireMinutes(String key,String value,int expireMinutes);

    String get(String key);

}
