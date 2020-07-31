package com.learn.lilintong.apitoken.common;

import lombok.Getter;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Getter
public enum CustomErrorCode implements IErrorCode {
    /**
     * 0: 失败 1: 成功 100：参数格式错误 101：token失效 102：非法请求 103：服务繁忙，请重试
     * 104：请求已处理，但部分请求失败 105：服务调用失败 106：设备序号不存在 107：门禁设备不存在 108：设备IP不存在
     * 109：租户信息不存在 110：下级平台内部错误 120：需要下发更新蓝牙摘要
     */
    SUCCESS("1", "成功"),

    ERROR("0", "失败"),

    PARAMETER_ERROR("100", "参数格式错误"),

    ILLEGAL_REQUEST("102", "非法请求"),

    SERVER_BUSY("103", "服务繁忙，请重试"),

    PARTIAL_FAILURE("104", "请求已处理，但部分请求失败"),

    REQUEST_ERROR("105", "服务调用失败"),

    SN_NOT_EXISTS("106", "设备序号不存在"),

    DOOR_DEVICE_NOT_EXISTS("107", "门禁设备不存在"),

    IP_NOT_EXISTS("108", "设备IP不存在"),

    TENANT_NOT_EXISTS("109", "租户信息不存在"),

    SERVICE_ERROR("110", "下级平台内部错误"),

    NEED_UPDATE_BLUETOOTH("120", "需要下发更新蓝牙摘要"),

    TOKEN_ERROR("101", "token失效"),

    GET_FACE_PIC_ERROR("301", "获取图片失败，需重新下发"),

    ADD_REGION_ERROR("111", "添加门禁机对应区域失败"),

    FACE_PIC_QUALITY_NOT_PASS("302", "图片不符合人脸要求，需重采"),

    FACE_PIC_PASSER_ERROR("303", "图片解析失败"),

    OTHER_ERROR("309", "其他"),


    CONNOINCT_CON_OBJ_NULL("1002", "数据库连接对象为空(内部错误)"),

    PG_EXCEPT_SQL_FAILED("1003", "执行Sql语句异常"),

    UP_LOAD_ALARM_EVENT_FAIL("1004", "上传报警时间失败"),

    UP_LOAD_INREPORT_FAIL("1005", "批量上报进出记录失败"),

    PARAM_IS_NULL("1006", "必传参数为空！"),

    GET_WAY_OUT_TIME("1007", "网络出问题或者请求超时"),

    CACLE_CARD_SUCCESS("1008", "注销卡成功"),

    CACLE_CARD_FAILED("1009", "注销卡失败"),

    EXCEPTION_INFO("1010", "服务出现异常"),

    FACE_UPDATE_FAILED("1011", "人脸更新失败"),

    FACE_CHECK_FAILED("1012", "人脸检测失败"),

    SAVE_LOCAL_DATA_FAILED("1013", "保存到本地数据失败"),

    FROM_ISC_GET_PIC_FAILED("1014","从isc获取图片失败");

    String code;

    String message;

    CustomErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
