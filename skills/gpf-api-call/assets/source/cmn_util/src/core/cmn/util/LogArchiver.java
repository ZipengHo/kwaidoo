package cmn.util;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogArchiver {
    private final String logFilePath;
    private final String archiveDir;
    private final int keepDays;
    private final String originalFileName;
    private final String baseName;
    private final String extension;
    
    // 日期格式化器，用于生成归档文件名
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public LogArchiver(String logFilePath, String archiveDir, int keepDays) {
        this.logFilePath = logFilePath;
        this.archiveDir = archiveDir;
        this.keepDays = keepDays;
        
        // 解析原始日志文件的名称和扩展名，用于清理时匹配
        File logFile = new File(logFilePath);
        this.originalFileName = logFile.getName();
        if (this.originalFileName.contains(".")) {
            this.baseName = this.originalFileName.substring(0, this.originalFileName.lastIndexOf('.'));
            this.extension = this.originalFileName.substring(this.originalFileName.lastIndexOf('.'));
        } else {
            this.baseName = this.originalFileName;
            this.extension = "";
        }
    }
    
    /**
     * 启动定时任务，每天0点执行日志归档
     */
    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // 计算距离明天0点的时间（毫秒）
        long delay = calculateDelayToMidnight();
        
        // 每天执行一次
        scheduler.scheduleAtFixedRate(this::archiveLogs, delay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
        
        System.out.println("日志归档服务已启动，将在每天0点执行归档");
    }
    
    /**
     * 归档日志文件的核心方法
     */
    public void archiveLogs() {
        File logFile = new File(logFilePath);
        
        // 检查日志文件是否存在
        if (!logFile.exists()) {
            System.out.println("日志文件不存在: " + logFilePath);
            return;
        }
        
        // 检查日志文件是否为空
        if (logFile.length() == 0) {
            System.out.println("日志文件为空，无需归档");
            return;
        }
        
        try {
            // 确保归档目录存在
            Files.createDirectories(Paths.get(archiveDir));
            
            // 生成归档文件名（原文件名+日期）
            String archiveFileName = baseName + "_" + 
                LocalDate.now().minusDays(1).format(DATE_FORMATTER) + extension;
            String archiveFilePath = archiveDir + File.separator + archiveFileName;
            
            // 复制日志内容到归档文件
            Files.copy(Paths.get(logFilePath), Paths.get(archiveFilePath), 
                StandardCopyOption.REPLACE_EXISTING);
            
            // 清空原日志文件
            try (PrintWriter writer = new PrintWriter(logFile)) {
                writer.print("");
            }
            
            System.out.println("日志已归档至: " + archiveFilePath);
            
            // 删除过期的归档文件
            cleanOldArchives();
            
        } catch (IOException e) {
            System.err.println("归档日志时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 删除超过保留天数的归档文件
     * 改进点：精确匹配归档文件，增强日期解析容错性
     */
    private void cleanOldArchives() {
        File dir = new File(archiveDir);
        
        // 只过滤以原始文件名前缀、包含日期格式且有正确扩展名的文件
        File[] archives = dir.listFiles((d, name) -> {
            // 检查文件名格式是否匹配：原始前缀_日期.扩展名
            String expectedPattern = baseName + "_\\d{8}" + extension;
            return name.matches(expectedPattern);
        });
        
        if (archives == null || archives.length == 0) {
            System.out.println("没有需要清理的归档文件");
            return;
        }
        
        LocalDate cutoffDate = LocalDate.now().minusDays(keepDays);
        System.out.println("开始清理" + cutoffDate + "之前的归档文件...");
        
        for (File archive : archives) {
            try {
                // 从文件名中提取日期部分
                String fileName = archive.getName();
                // 计算日期字符串的起始位置（基础名称长度 + 下划线）
                int dateStartIndex = baseName.length() + 1;
                int dateEndIndex = dateStartIndex + 8; // 日期格式是8位数字
                
                // 检查是否有足够长度的日期部分
                if (dateEndIndex > fileName.length()) {
                    System.out.println("跳过格式异常的文件: " + fileName);
                    continue;
                }
                
                String dateStr = fileName.substring(dateStartIndex, dateEndIndex);
                LocalDate archiveDate = LocalDate.parse(dateStr, DATE_FORMATTER);
                
                // 如果归档文件超过保留天数，则删除
                if (archiveDate.isBefore(cutoffDate)) {
                    if (archive.delete()) {
                        System.out.println("已删除过期归档: " + archive.getAbsolutePath());
                    } else {
                        System.err.println("无法删除过期归档: " + archive.getAbsolutePath());
                    }
                }
            } catch (DateTimeParseException e) {
                System.err.println("文件 " + archive.getName() + " 的日期格式不正确，跳过处理: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("处理归档文件 " + archive.getName() + " 时出错: " + e.getMessage());
            }
        }
    }
    
    /**
     * 计算距离明天0点的毫秒数
     */
    private long calculateDelayToMidnight() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        long midnight = tomorrow.atStartOfDay().toEpochSecond(java.time.ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now())) * 1000;
        long now = System.currentTimeMillis();
        return midnight - now;
    }
    
    public static void main(String[] args) {
        // 示例用法
        String logFilePath = "/path/to/your/logfile.log";
        String archiveDir = "/path/to/archive/directory";
        int keepDays = 7; // 保留7天的归档
        
        LogArchiver archiver = new LogArchiver(logFilePath, archiveDir, keepDays);
        archiver.start();
    }
}
    
    