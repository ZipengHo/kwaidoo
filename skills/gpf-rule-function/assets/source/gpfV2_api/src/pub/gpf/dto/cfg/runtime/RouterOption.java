package gpf.dto.cfg.runtime;

import java.io.Serializable;
import java.util.Set;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;

@ClassDeclare(label = "路由选项",
        what = "节点扩展时启用自主路由时，用于流程流转的路由选项",
        why = "",
        how = "",
        developer = "陈晓斌",
        version = "1.0",
        createTime = "2025-03-27",
        updateTime = "2025-03-27")
public class RouterOption implements Serializable {

    private static final long serialVersionUID = -6022542045588361267L;

    @FieldDeclare(label = "离开路由包含所有下游节点", desc = "优先级高于nexts")
    boolean goNextAll = false;

    @FieldDeclare(label = "下一步节点Key列表", desc = "")
    Set<String> nexts;

    @FieldDeclare(label = "跳转下一步前重置状态的节点列表", desc = "")
    Set<String> resetBefore;

    @FieldDeclare(label = "跳转下一步后重置状态的节点", desc = "需要注意的是：NodeOption如果开启了autoGoNext时，自动提交为同步操作时重置节点不能包含自身，否则重置无效")
    Set<String> resetAfter;

    public boolean isGoNextAll() {
        return goNextAll;
    }

    public RouterOption setGoNextAll(boolean goNextAll) {
        this.goNextAll = goNextAll;
        return this;
    }

    public Set<String> getNexts() {
        return nexts;
    }

    public Set<String> getResetBefore() {
        return resetBefore;
    }

    public Set<String> getResetAfter() {
        return resetAfter;
    }

    public RouterOption setNexts(Set<String> nexts) {
        this.nexts = nexts;
        return this;
    }

    public RouterOption setResetBefore(Set<String> resetBefore) {
        this.resetBefore = resetBefore;
        return this;
    }

    public RouterOption setResetAfter(Set<String> resetAfter) {
        this.resetAfter = resetAfter;
        return this;
    }
}
