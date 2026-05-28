package cmn.enums;

public enum ErrorLevel {
	// 致命级别，通常表示系统无法继续运行，需要立即人工介入处理
    FATAL(1, "致命异常，系统可能崩溃，需立即处理"),

    // 错误级别，代表业务逻辑或系统功能出现了严重问题，但系统仍可继续运行
    ERROR(2, "错误异常，业务功能受影响，需及时排查"),

    // 警告级别，意味着可能存在潜在的问题，但目前不会对系统的正常运行造成严重影响
    WARN(3, "警告异常，存在潜在风险，需关注"),

    // 信息级别，主要用于记录一些正常流程中的异常情况，一般不会影响系统运行
    INFO(4, "信息异常，正常流程中的异常情况，可忽略");

    // 异常级别的代码，方便存储和识别
    private final int code;
    // 异常级别的描述信息，便于理解每个级别的含义
    private final String description;

    // 构造方法，用于初始化异常级别的代码和描述信息
    ErrorLevel(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取异常级别代码的方法
    public int getCode() {
        return code;
    }

    // 获取异常级别描述信息的方法
    public String getDescription() {
        return description;
    }

}
