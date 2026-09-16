package com.micro.cloud.common.core.constant;

/**
 * 全局常量
 */
public interface Constants {

    /** 链路追踪 ID 请求头 */
    String TRACE_ID_HEADER = "X-Trace-Id";

    /** MDC 链路追踪 ID 键 */
    String TRACE_ID_KEY = "traceId";

    /** 成功标记 */
    int SUCCESS = 200;

    /** 失败标记 */
    int FAIL = 500;

    /** 未授权 */
    int UNAUTHORIZED = 401;

    /** 禁止访问 */
    int FORBIDDEN = 403;

    /** 登录成功 */
    String LOGIN_SUCCESS = "0";

    /** 登录失败 */
    String LOGIN_FAIL = "1";

    /** 正常状态 */
    String STATUS_NORMAL = "0";

    /** 停用状态 */
    String STATUS_DISABLE = "1";

    /** 删除标志-存在 */
    String DEL_FLAG_EXIST = "0";

    /** 删除标志-删除 */
    String DEL_FLAG_DELETED = "2";
}
