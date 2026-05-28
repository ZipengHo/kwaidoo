package basic.cmn.event;

import com.kwaidoo.ms.tool.CmnUtil;

import java.io.Serializable;
import java.security.SecureRandom;

/**
 * 追踪的核心元数据，格式为：version-traceId-parentId-traceFlags
 */
public class TraceContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static ThreadLocal<String> currentSpanIdLocal = new ThreadLocal<>();
    /**
     * 当前规范版本
     */
    String version = "00";
    /**
     * 全局唯一的追踪 ID（16 字节，32 位十六进制），标识整个分布式请求链；
     */
    String traceId;
    /**
     * 当前服务的父级 Span ID（8 字节，16 位十六进制），即上游服务的 Span ID；
     */
    String parentId = "";
    /**
     * 1 字节标志位（如01表示启用采样，00表示不采样）。
     */
    String traceFlags = "01";

    String traceState;

    public TraceContext(){
        this.traceId = generateRandom32Hex();
    }

    public TraceContext(String traceParent){
        if (CmnUtil.isStringEmpty(traceParent)) {
            this.traceId = generateRandom32Hex();
        }else {
            String[] parts = traceParent.split("-");
            if (parts.length != 4) {
                this.traceId = generateRandom32Hex();
            }else {
                this.version = parts[0];
                this.traceId = parts[1];
                this.parentId = parts[2];
                this.traceFlags = parts[3];
            }
        }
    }
    /**
     * 获取当前线程的 Span ID，如果不存在则生成一个新的
     * @return 当前线程的 Span ID
     */
    public static String getCurrentSpanId(){
        String currentSpanId = currentSpanIdLocal.get();
        if (CmnUtil.isStringEmpty(currentSpanId)) {
            currentSpanId = generateRandom16Hex();
            currentSpanIdLocal.set(currentSpanId);
        }
        return currentSpanId;
    }

    /**
     * 生成符合 W3C Trace Context 规范的 trace-id（32 位十六进制字符串）
     * 对应 16 字节（128 位）随机数
     */
    public static String generateRandom32Hex() {
        byte[] bytes = new byte[16]; // 16 字节 = 128 位
        SECURE_RANDOM.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    /**
     * 生成符合 W3C Trace Context 规范的 span-id（16 位十六进制字符串）
     * 对应 8 字节（64 位）随机数
     */
    public static String generateRandom16Hex() {
        byte[] bytes = new byte[8]; // 8 字节 = 64 位
        SECURE_RANDOM.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    /**
     * 将字节数组转换为十六进制字符串（小写）
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF; // 转换为无符号整数
            hexChars[i * 2] = HEX_CHARS[v >>> 4]; // 高 4 位
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F]; // 低 4 位
        }
        return new String(hexChars);
    }

    public String getVersion() {
        return version;
    }

    public TraceContext setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public TraceContext setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public TraceContext setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getTraceFlags() {
        return traceFlags;
    }

    public TraceContext setTraceFlags(String traceFlags) {
        this.traceFlags = traceFlags;
        return this;
    }

    public String getTraceState() {
        return traceState;
    }

    public TraceContext setTraceState(String traceState) {
        this.traceState = traceState;
        return this;
    }

    @Override
    public String toString() {
        return version + "-" + traceId + "-" + parentId + "-" + traceFlags;
    }
    /**
     * 获取当前线程的 Trace Parent 字符串，格式为：version-traceId-currentSpanId-traceFlags
     * @return 当前线程的 Trace Parent 字符串
     */
    public String getCurrentTraceParent(){
        return version + "-" + traceId + "-" + getCurrentSpanId() + "-" + traceFlags;
    }
}
