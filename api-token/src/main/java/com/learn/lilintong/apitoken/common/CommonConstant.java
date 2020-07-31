package com.learn.lilintong.apitoken.common;

/**
 * 公共常量
 *
 */
public interface CommonConstant {
    // 图片后缀
    String PIC_SUFFIX = ".jpg";

    /**
     * person缓存前缀
     */
    String REDIS_PERSON_CACHE = "person-";
    /**
     * Org缓存前缀
     */
    String REDIS_ORG_CACHE = "org-";
    /**
     * 人员卡片缓存前缀
     */
    String REDIS_TENEMENT_CARD_RELATION_CACHE = "tcr-";
    /**
     * token缓存
     */
    String REDIS_TOKEN_CACHE = "token-";

    /**
     * 区域缓存前缀
     */
    String REDIS_REGION_CACHE = "region-";

    /**
     * 区域缓存前缀
     */
    String REDIS_VIS_DEVICE_CACHE = "vis-device-";

    /**
     * region 缓存过期时间
     */
    int REDIS_REGION_TIME_OUT = 1;

    /**
     * region 缓存过期时间
     */
    int REDIS_VIS_DEVICE_TIME_OUT = 1;

    /**
     * token缓存
     */
    String REDIS_HEARTBEAT_CACHE = "ctm05xyvads_heartbeat";
    /**
     * token
     */
    String TOKEN = "token";
    /**
     * appId
     */
    String APPID = "appId";
    /**
     * person 缓存过期时间
     */
    int REDIS_PERSON_TIME_OUT = 2;
    /**
     * 人员变更消息变更类型
     */
    String OPERATE_ADD = "add";
    String OPERATE_UPDATE = "update";
    String OPERATE_DELETE = "delete";

    /**
     * 人员修改状态: 0-新增 1-修改 2-删除
     */
    int PERSON_ADD = 0;
    int PERSON_UPDATE = 1;
    int PERSON_DELETE = 2;

    String HIK_SALT = "hik963852741";
}
