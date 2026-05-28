//package cell.bap.test;
//// 测试HOOK时自动生成的代码如果有编译问题， 可以粘贴到这里测试
//
//import cell.*;
//import java.util.HashMap;
//import com.leavay.common.util.*;
//import com.leavay.common.util.javac.ClassFactory;
//import bap.cells.*;
//public class TestDaoImpl_Wrapper extends cell.bap.test.TestDaoImpl implements bap.cells.CellWrapperIntf{
//    protected transient CellIntf _realMe;
//    public CellIntf getRealCell(){return _realMe;}
//    public TestDaoImpl_Wrapper(Object realMe){this._realMe=(CellIntf)realMe;}
//public java.lang.Object test(java.lang.Object p0) throws java.lang.Exception{
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "test", p0);
//    
//    try{
//        return (java.lang.Object)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "test", p0);
//    }finally{
//    }
//}
//
//public void setTimeout(long p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "setTimeout", p0);
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "setTimeout", p0);
//    }finally{
//    }
//}
//
//public void resetConfig(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "resetConfig");
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "resetConfig");
//    }finally{
//    }
//}
//
//public bap.cells.CellConfig getConfig(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getConfig");
//    
//    try{
//        return (bap.cells.CellConfig)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getConfig");
//    }finally{
//    }
//}
//
//public long getTimeout(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getTimeout");
//    
//    try{
//        return (java.lang.Long)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getTimeout");
//    }finally{
//    }
//}
//
//public java.lang.String toString(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "toString");
//    
//    try{
//        return (java.lang.String)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "toString");
//    }finally{
//    }
//}
//
//public void setCallbackUuid(java.lang.String p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "setCallbackUuid", p0);
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "setCallbackUuid", p0);
//    }finally{
//    }
//}
//
//public java.lang.String getCallbackUuid(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getCallbackUuid");
//    
//    try{
//        return (java.lang.String)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getCallbackUuid");
//    }finally{
//    }
//}
//
//public long getLastAccess(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getLastAccess");
//    
//    try{
//        return (java.lang.Long)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getLastAccess");
//    }finally{
//    }
//}
//
//public void updateLastAccess(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "updateLastAccess");
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "updateLastAccess");
//    }finally{
//    }
//}
//
//public long getExpireTime(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExpireTime");
//    
//    try{
//        return (java.lang.Long)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExpireTime");
//    }finally{
//    }
//}
//
//public void setExpireTime(long p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "setExpireTime", p0);
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "setExpireTime", p0);
//    }finally{
//    }
//}
//
//
//public boolean equals(java.lang.Object p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "equals", p0);
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "equals", p0);
//    }finally{
//    }
//}
//
//public boolean isAlive(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "isAlive");
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "isAlive");
//    }finally{
//    }
//}
//
//public void close(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "close");
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "close");
//    }finally{
//    }
//}
//
//public boolean isProxy(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "isProxy");
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "isProxy");
//    }finally{
//    }
//}
//
//public crpc.CRpcCallbackStub _proxyGetRemoteStub(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "_proxyGetRemoteStub");
//    
//    try{
//        return (crpc.CRpcCallbackStub)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "_proxyGetRemoteStub");
//    }finally{
//    }
//}
//
//public void releaseLocalCache(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "releaseLocalCache");
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "releaseLocalCache");
//    }finally{
//    }
//}
//
//public void _proxySetRemoteStub(crpc.CRpcCallbackStub p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "_proxySetRemoteStub", p0);
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "_proxySetRemoteStub", p0);
//    }finally{
//    }
//}
//
//public boolean getExtBoolean(java.lang.String p0 ,boolean p1){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtBoolean", p0 ,p1);
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtBoolean", p0 ,p1);
//    }finally{
//    }
//}
//
//public java.lang.Object getExtFields(java.lang.String p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtFields", p0);
//    
//    try{
//        return (java.lang.Object)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtFields", p0);
//    }finally{
//    }
//}
//
//public java.util.HashMap getExtFields(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtFields");
//    
//    try{
//        return (java.util.HashMap)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtFields");
//    }finally{
//    }
//}
//
//public java.lang.String getOrAllocUuid(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getOrAllocUuid");
//    
//    try{
//        return (java.lang.String)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getOrAllocUuid");
//    }finally{
//    }
//}
//
//public java.lang.String getExtString(java.lang.String p0 ,java.lang.String p1){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtString", p0 ,p1);
//    
//    try{
//        return (java.lang.String)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtString", p0 ,p1);
//    }finally{
//    }
//}
//
//public long getExtLong(java.lang.String p0 ,long p1){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtLong", p0 ,p1);
//    
//    try{
//        return (java.lang.Long)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtLong", p0 ,p1);
//    }finally{
//    }
//}
//
//public int getExtInt(java.lang.String p0 ,int p1){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getExtInt", p0 ,p1);
//    
//    try{
//        return (java.lang.Integer)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getExtInt", p0 ,p1);
//    }finally{
//    }
//}
//
//public boolean isSingleton(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "isSingleton");
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "isSingleton");
//    }finally{
//    }
//}
//
//public void initCell(java.lang.Object[] p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "initCell", p0);
//    
//    try{
//    bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "initCell", p0);
//    }finally{
//    }
//}
//
//public java.lang.Object getConfig(java.lang.Class p0){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "getConfig", p0);
//    
//    try{
//        return (java.lang.Object)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "getConfig", p0);
//    }finally{
//    }
//}
//
//public boolean isSupportBridge(){
//    long _____id = ToolBasic.allocRandomID();
//   bap.cells.CellHookProvider.executeHook(CellHookIntf.ENTRY.BEFORE, _____id, "bap.cells.hook.CellHookPrinter", getRealCell(), "isSupportBridge");
//    
//    try{
//        return (java.lang.Boolean)bap.cells.CellHookProvider.executeRealFunction(getRealCell(), "isSupportBridge");
//    }finally{
//    }
//}
//
//}