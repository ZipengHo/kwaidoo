package cell.cdao;

import java.net.URI;
import java.util.List;
import java.util.Set;

import com.cdao.mgr.BroadcastBoard;
import com.cdao.mgr.CDaoCenterIntf;
import com.cdao.mgr.CDaoEntity;
import com.cdao.mgr.CSession;
import com.cdao.mgr.ClassInheritNode;
import com.cdao.mgr.ModelObserver;
import com.cdao.model.CDoBasic;
import com.cdao.model.CDoModel;
import com.cdao.model.CDoUser;
import com.leavay.common.util.Pair;
import com.leavay.common.util.ProgressCtrl.crpc.IProgress;

import bap.cells.Cells;
import cell.ServiceCellIntf;

/**
 * 对DAO操作的服务Cell，使用无需释放
 */
public interface IDaoService extends ServiceCellIntf, CDaoCenterIntf
{
    public static IDaoService get()
    {
        return Cells.get(IDaoService.class);
    }
    
    public static IDao newIDao() throws Exception
    {
        return get().newDao();
    }
    
    /**
     * 获取根据时钟同步估算的中心时间（有一定误差以及不同步的风险）
     */
    public long getEstimateCenterTime();
    
    // 可否作为模型基类（主要是动态类创建界面等才需要此判断）
    public boolean isValidBasicModel(String cls);
    
    // 启动时初始化固定DAO模型
    public void addInitModel(Class ... clsArray);
    
    public URI getServerUri();
    
    // 创建Dao连接，使用后必须close
    public IDao newDao() throws Exception;
    public IDao newDao(CSession session) throws Exception;
    
    // 获取模型的entity，会有缓存，性能较好，但在修改模型的间隙有可能脏
    public CDaoEntity getEntity(String className) throws Exception;

    // 实时加载模型的entity定义（结合数据库信息），信息真实准确，但无缓存损耗一定性能
    public CDaoEntity loadEntity(String className) throws Exception;
    

    public CDoBasic newDo(String className) throws Exception;

    public long getClassTimeTag();
    public List<String> getInheritTree(String parentClass, boolean includeSelf);

    public BroadcastBoard getBroadcast();
    public Object getBroadcast(String key);
    
    public Set<String> getForeignChild(String parentClass);
    
    // 搜索给定模型到底可以依存于那些外键主表
    public Set<String> getForeignParent(String childClass);
    
    // 判断是否被人用作外键父
    public boolean isForeignParent(String parentClass);
    
    // 判断两个类之间是否可以建立外键组合关系
    public boolean isValidForeign(String childClass, String parentClass);
    
    /**
     * 搜索某个类是否有被其它投影字段引用，含所有子类（为了性能，有可能返回NULL）
     * (等待优化，可以在模型管理里缓存，快速找到被投影引用的字段)
     * 
     * 返回Left是类名，Right是字段名
     */
    public List<Pair<String, String>> getProjectionRelation(String srcClass);
    
    /**
     * 搜索某个类的某个字段，是否有被其它投影字段引用，含所有子类（为了性能，有可能返回NULL）
     * (等待优化，可以在模型管理里缓存，快速找到被投影引用的字段)
     * 
     * 返回Left是类名，Right是字段名
     */
    public List<Pair<String, String>> getProjectionRelation(String targetClass, String field) throws ClassNotFoundException;

    // 校验用户密码，并返回完整的用户数据
    public CDoUser loginWithUser(String userName, String pwd) throws Exception;
    
    // 校验用户密码，并返回Cession信息
    public CSession loginWithSession(String userName, String pwd) throws Exception;
    
    public boolean isCenterServer();
    
    public boolean isCenterPackageReady();
    
    public ClassInheritNode searchModelNode(String fullClassName);
    
    public ClassInheritNode getInheritTreeRoot();

    // 判断一个模型是否静态模型(即静态代码加载的模型)
    public boolean isStaticCodeModel(String fullClassName);
    
    /**
     *  强行和DAO中心进行同步（含模型包、派生树、监听器），用于一些极特殊场景，例如刚刚增删改模型，就要立刻访问最新的信息等
     *  
     *  注：此动作会有一定性能损耗以及阻塞作用，不可频繁调用
     */
    public void forceSyncWithCenter() throws Exception;

    /**
     * 查询DAO主控中心缓存（简单内存缓存，无复杂同步机制，无持久化，无监听适用于轮询，且主控中心必须可连通）
     */
    public Object getCenterCache(String key);

    /**
     * 在DAO主控中心缓存中写入键值（简单内存缓存，无复杂同步机制，无持久化，无监听适用于轮询，且主控中心必须可连通）
     */
    public void putCenterCache(String key, Object value);

    /**
     * 在DAO主控中心缓存中删除键值（简单内存缓存，无复杂同步机制，无持久化，无监听适用于轮询，且主控中心必须可连通）
     */
    public Object deleteCenterCache(String key);
    

    /**
     * 创建模型并阻塞等待类加载器更新
     * timeOut == -1 则表示永久等待, 永不超时(危险)
     */
    public CDoModel createModelBlocked(CDoModel md, long timeOut) throws Exception;
    
    /**
     * 修改模型并阻塞等待类加载器更新
     * timeOut == -1 则表示永久等待, 永不超时(危险)
     */
    public long updateModelBlocked(CDoModel md, long timeOut) throws Exception;
    

    /**
     * 
     * 批量导入模型并等待模型发布更新，其中涉及若干新增、若干修改、互相编译级耦合、以及关联的子类、依赖类等等
     * 这里会等待新的模型包发布到当前客户端, 并更新CDaoClient后才返回
     * 
     * @param observer : 用于干涉模型导入整个过程的观察者，会被发送到后端并只会运行在DAO中心进程内
     *                                      该类必须存在于DAO中心进程（或插件中），不能用脚本等动态匿名类来传递
     *  @return 模型变更后最新的时间戳
     */
    public long importModelsBlocked(IProgress prog, List<CDoModel> lstModels, ModelObserver observer,  long timeOut) throws Exception;
}
