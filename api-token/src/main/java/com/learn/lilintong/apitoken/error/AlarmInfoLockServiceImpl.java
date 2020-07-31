package com.learn.lilintong.apitoken.error;

import com.alibaba.fastjson.JSON;
import com.hik.sz.common.util.*;
import com.hik.sz.dao.AlarmHandleDao;
import com.hik.sz.dao.LockPoolInfoDao;
import com.hik.sz.model.entity.LockInfo;
import com.hik.sz.model.entity.LockPoolDo;
import com.hik.sz.model.entity.RecordInfo;
import com.hik.sz.model.response.ApiResult;
import com.hik.sz.model.response.LockPoolInfo;
import com.hik.sz.proxy.AlarmInfoLockService;
import com.hikvision.starfish.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lilintong
 * @create 2020/5/7
 */
@Slf4j
@Service
public class AlarmInfoLockServiceImpl {

    @Value("${com.hikcstor.settings.serverIp}")
    private String ip;

    @Value("${com.hikcstor.settings.serverPort}")
    private String port;

    @Value("${com.hikcstor.settings.accessKey}")
    private String accessKey;

    @Value("${com.hikcstor.settings.secretKey}")
    private String secretKey;

    private static final String APPLICATION_JSON_VALUE = "application/json";

    private static final String HTTP_PROTOCOL = "http://";

    private static final String LOCK_PIC_URI = "/HikCStor/Picture/Lock";

    private static final String LOCK_VIDEO_URI = "/ISAPI/Storage/lockSegment";

    private static final String QUERY_LOCK_POOL_URI = "/ISAPI/Storage/lockInfo";

    private static final String QUERY_SEGMENT_URI = "/ISAPI/Storage/querySegment";


    @Autowired
    AlarmHandleDao alarmHandleDao;

    @Autowired
    LockPoolInfoDao lockPoolInfoDao;

    @Autowired
    private RequestConfig requestConfig;

    @Override
    public void pictureLock(String picUrl, int lockTime, Integer id) {
        CloseableHttpResponse response = null;
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        try {
            String endpoint = ip + ":" + port;
            String encodeUrl = HTTP_PROTOCOL + endpoint + LOCK_PIC_URI;
            HttpPost httpPost = new HttpPost(encodeUrl);

            // 设置请求体
            Map<String, String> paramMap = new HashMap<>(2);
            paramMap.put("URL", picUrl);
            paramMap.put("LockTime", String.valueOf(lockTime));
            String requestParam = JSON.toJSONString(paramMap);
            //对请求体计算MD5
            String contentMd5 = MD5Utils.EncoderByMd5(requestParam);

            setHeader(httpPost, endpoint, "POST", contentMd5, LOCK_PIC_URI, "hikcstor", 0, APPLICATION_JSON_VALUE);

            StringEntity stringEntity = new StringEntity(requestParam);
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(requestConfig);

            response = closeableHttpClient.execute(httpPost);

            if(response != null){
                int responseCode = response.getStatusLine().getStatusCode();
                if(200 == responseCode){
                    // 图片锁定成功则更新图片锁定状态
                    alarmHandleDao.updateAlarmHandleDoLockPictureStatusById(id, 1);
                }else {
                    String msg = handleErrorRequest(response);
                    log.error("lock picture fail, picUrl:{}, http error msg:{}, id:{}", picUrl, msg, id);
                }
            }

        } catch (Exception ex) {
            log.error("lock video have an Exception: {}, picUrl:{}, id:{}", ex, picUrl, id);
        } finally {
            release(response, closeableHttpClient);
        }
    }


    @Override
    public void videoLock(LockInfo info, Integer id) {
        CloseableHttpResponse response = null;
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        try {
            // host请求头
            String endpoint = ip + ":" + port;
            String queryParam = buildLockVideoParam(info);

            String url = LOCK_VIDEO_URI + "?" + queryParam;
            String encodeUrl = HTTP_PROTOCOL + endpoint + LOCK_VIDEO_URI + "?" + URLEncoder.encode(queryParam, "utf-8");

            HttpPut httpPut = new HttpPut(encodeUrl);
            setHeader(httpPut, endpoint, "PUT","", url, "storage", 1, APPLICATION_JSON_VALUE);

            httpPut.setConfig(requestConfig);
            response = closeableHttpClient.execute(httpPut);

            if(response != null){
                int responseCode = response.getStatusLine().getStatusCode();
                if(200 == responseCode){
                    // 确认视频是否锁定成功
                    AlarmInfoLockService bean = SpringUtil.getBean(this.getClass());
                    bean.confirmVideoLock(info, id);
                }else {
                    String msg = handleErrorRequest(response);
                    log.error("lock video fail, LockInfo:{} http error msg{}, id:{}", JsonUtil.toJson(info), msg, id);
                }
            }

        } catch (Exception ex) {
            log.error("lock video have an Exception: {}, LockInfo:{}, id:{}", ex, JsonUtil.toJson(info), id);
        } finally {
            release(response, closeableHttpClient);
        }

    }

    /**
     * 确认失败重试三次,间隔15s,退避指数2
     * @param info
     * @param id
     */
    @Override
    @Async("asyncServiceExecutor")
    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000 * 15, multiplier = 2))
    public void confirmVideoLock(LockInfo info, Integer id){
        CloseableHttpResponse response = null;
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        try {
            String endpoint = ip + ":" + port;
            String queryParam = buildQueryParam(info);

            String url = QUERY_SEGMENT_URI + "?" + queryParam;
            String encodeUrl = HTTP_PROTOCOL + endpoint + QUERY_SEGMENT_URI + "?" + URLEncoder.encode(queryParam, "utf-8");

            HttpGet httpget = new HttpGet(encodeUrl);
            httpget.setConfig(requestConfig);

            setHeader(httpget, endpoint, "GET","", url, "storage", 1, APPLICATION_JSON_VALUE);

            response = closeableHttpClient.execute(httpget);
            if(response != null){
                int responseCode = response.getStatusLine().getStatusCode();
                if(200 == responseCode){
                    List<RecordInfo> list = JsonParser.parseRecordListResponse(response);
                    // 视频锁定成功则更新视频锁定状态
                    if(CollectionUtils.isNotEmpty(list)){
                        alarmHandleDao.updateAlarmHandleDoLockVideoStatusById(id, 1);
                        return;
                    }
                }else {
                    String msg = handleErrorRequest(response);
                    log.error("query video fail, LockInfo:{} http error msg{}, id:{}", JsonUtil.toJson(info), msg, id);
                }
                throw new RetryException("retry");
            }

        } catch (Exception ex) {
            log.error("query video have an Exception: {}, LockInfo:{}, id:{}", ex, JsonUtil.toJson(info), id);
            throw new RetryException("retry");
        } finally {
            release(response, closeableHttpClient);
        }

    }


    @Recover
    public void recover(RetryException e) {
        log.error("重试视频锁定确认方法已到达次数");
    }


    @Override
    public ApiResult<List<LockPoolInfo>> getLockPoolInfoList() {
        List<LockPoolInfo> poolInfoList = new ArrayList<>();
        // 数据库先查出资源池列表
        List<LockPoolDo> poolDoList = lockPoolInfoDao.listLockPoolDo();
        List<String> poolIdList = poolDoList.stream().map(it -> it.getPoolId()).collect(Collectors.toList());
       for(String id: poolIdList){
           LockPoolInfo lockPoolInfo = queryLockPoolInfo(id);
           if(lockPoolInfo != null){
               // 计算当前已锁定的比例
               BigDecimal lockedSize = new BigDecimal(lockPoolInfo.getPoolLockedSize());
               BigDecimal freeSize = new BigDecimal(lockPoolInfo.getFreeLockSize());
               BigDecimal total = freeSize.add(lockedSize);
               BigDecimal port = lockedSize.divide(total,2, BigDecimal.ROUND_UP);
               BigDecimal locked = port.multiply(new BigDecimal(100));
               int lockedLimit = locked.setScale(0, BigDecimal.ROUND_UP).intValue();
               lockPoolInfo.setLockedLimit(lockedLimit);
               poolInfoList.add(lockPoolInfo);
           }
       }
        ApiResult<List<LockPoolInfo>> result = new ApiResult();
        result.setResult(poolInfoList);
        result.setCode("0");
        result.setMsg("查询成功");
        return result;
    }

    private LockPoolInfo queryLockPoolInfo(String poolId) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        LockPoolInfo lockPoolInfo = null;
        CloseableHttpResponse response = null;
        try {
            // host值
            String endpoint = ip + ":" + port;

            String queryParam = "&poolID=" + poolId;

            String url = QUERY_LOCK_POOL_URI + "?" + queryParam;

            String encodeUrl = HTTP_PROTOCOL + endpoint + QUERY_LOCK_POOL_URI + "?" + URLEncoder.encode(queryParam, "utf-8");

            HttpGet httpGet = new HttpGet(encodeUrl);

            setHeader(httpGet, endpoint, "GET","" ,url, "storage",1, APPLICATION_JSON_VALUE);

            httpGet.setConfig(requestConfig);

            response = closeableHttpClient.execute(httpGet);

            if(response != null){
                int responseCode = response.getStatusLine().getStatusCode();
                if(200 == responseCode){
                    lockPoolInfo = JsonParser.parseLockPoolInfo(response);
                }else {
                    String msg = handleErrorRequest(response);
                    log.error("query Lock PoolInfo fail, http error msg:{}, poolId={}", msg, poolId);

                }
            }

        } catch (Exception ex) {
            log.error("query lock pool info have an Exception: {}, poolId={}", ex, poolId);
        } finally {
            release(response, closeableHttpClient);
        }
        return lockPoolInfo;
    }

    /**
     * 设置请求头
     * @param requestMethod
     * @param endpoint
     * @param httpVerb
     * @param url
     */
    private void setHeader(HttpRequestBase requestMethod, String endpoint, String httpVerb, String contentMd5, String url,
                           String component, int encryptionAlgorithm, String ContentType){
        requestMethod.setHeader("Host", endpoint);

        String date = TimeTransfer.getGmtTime();
        requestMethod.setHeader("Date", date);

        requestMethod.setHeader("Content-Type", ContentType);

        requestMethod.setHeader("Connection", "close");

        if(StringUtils.isNotEmpty(contentMd5)){
            requestMethod.setHeader("Content-MD5", contentMd5);
        }

        String auth = AuthProcess.getAuthorization(accessKey, secretKey, httpVerb, contentMd5,
                ContentType, date, url, component, encryptionAlgorithm);

        requestMethod.setHeader("Authorization", auth);
    }


    private String buildLockVideoParam(LockInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("&serialID=").append(info.getSerialId());
        sb.append("&startTime=").append(info.getStartTime());
        sb.append("&endTime=").append(info.getEndTime());
        sb.append("&lockTime=").append(String.valueOf(info.getLockTime()));
        sb.append("&fileKey=").append(info.getFileKey());
        if (null != info.getRecordType() && 0 != info.getRecordType().length()) {
            sb.append("&recordType=").append(info.getRecordType());
        }
        return sb.toString();
    }

    private String buildQueryParam(LockInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append(info.getSerialId());
        sb.append("&startTime=").append(info.getStartTime());
        sb.append("&endTime=").append(info.getEndTime());
        sb.append("&dataType=").append(1);//视频文件
        sb.append("&lockType=").append(2);//已锁定
        if (null != info.getRecordType() && 0 != info.getRecordType().length()) {
            sb.append("&recordType=").append(info.getRecordType());
        }
        return sb.toString();
    }

    private String handleErrorRequest(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream inStream = entity.getContent();
        String msg = JsonParser.parseErrorMsg(inStream);
        EntityUtils.consume(entity);
        return msg;
    }

    /**
     * 关闭连接
     * @param response
     * @param closeableHttpClient
     */
    private void release(CloseableHttpResponse response, CloseableHttpClient closeableHttpClient){
        try {
            if(response != null){
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("close response have Exception", e);
        }

        try {
            if(closeableHttpClient != null){
                closeableHttpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("close closeableHttpClient have Exception", e);
        }
    }


}
