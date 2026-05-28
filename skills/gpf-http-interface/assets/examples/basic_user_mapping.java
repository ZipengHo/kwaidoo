package cell.example.http;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingIntf;

@ClassDeclare(
    label = "用户HTTP接口",
    what = "提供用户查询相关的HTTP接口",
    why = "为前端提供用户基础查询能力",
    how = "通过HTTP GET请求访问/example/user路径下的接口",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/user")
public interface IUserHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "获取用户信息",
        what = "根据用户ID获取用户信息",
        why = "支持用户详情查询",
        how = "通过GET请求访问/example/user/{id}",
        inputs = {
            @InputDeclare(name = "id", label = "用户ID", desc = "用户唯一标识", nullable = false, exampleValue = "{id}")
        }
    )
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    UserInfo getUserInfo(String id) throws Exception;
}

package cell.example.http.impl;

import cmn.http.cells.BasicCell_RequestMapping;
import cell.example.http.IUserHttpMapping;

public class CUserHttpMapping extends BasicCell_RequestMapping implements IUserHttpMapping {

    @Override
    public UserInfo getUserInfo(String id) throws Exception {
        UserInfo user = new UserInfo();
        user.setId(id);
        user.setName("张三");
        return user;
    }
}
