package cell.example.http.file;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.multipart.MultipartFile;
import cmn.http.servlet.mapping.RequestMappingIntf;

@ClassDeclare(
    label = "文件HTTP接口",
    what = "提供文件上传和下载接口",
    why = "支持前端文件管理功能",
    how = "通过multipart上传和HTTP下载访问文件接口",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/file")
public interface IFileHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "上传文件",
        what = "上传单个文件到服务器",
        why = "支持文件上传功能",
        how = "通过POST请求访问/example/file/upload，使用multipart/form-data格式",
        inputs = {
            @InputDeclare(name = "file", label = "文件", desc = "待上传文件", nullable = false)
        }
    )
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    String uploadFile(MultipartFile file) throws Exception;

    @MethodDeclare(
        label = "下载文件",
        what = "根据文件名下载文件",
        why = "支持文件下载功能",
        how = "通过GET请求访问/example/file/download/{fileName}",
        inputs = {
            @InputDeclare(name = "fileName", label = "文件名", desc = "待下载文件名", nullable = false, exampleValue = "{fileName}")
        }
    )
    @RequestMapping(path = "/download/{fileName}", method = RequestMethod.GET)
    byte[] downloadFile(String fileName) throws Exception;
}
