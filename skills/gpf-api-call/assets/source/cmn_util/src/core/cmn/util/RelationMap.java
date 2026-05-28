package cmn.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;

import web.dto.Pair;

public class RelationMap implements Serializable
{
    private static final long serialVersionUID = -7228937860999807649L;

    protected Map<String, Set<String>> mapSrc2Dst = new  HashMap<String, Set<String>>();

    protected Map<String, Set<String>> mapDst2Src = new HashMap<String, Set<String>>();

    protected synchronized void clearSrc2Dst()
    {
        mapSrc2Dst.clear();
    }

    protected synchronized void addSrc2Dst(String source, Set<String> setDst)
    {
        mapSrc2Dst.put(source, setDst);
    }

    protected synchronized void removeSrc2Dst(String srcRDN)
    {
        mapSrc2Dst.remove(srcRDN);
    }

    public synchronized boolean hasTarget(String src)
    {
        return !ToolUtilities.isObjectEmpty(mapSrc2Dst.get(src));
    }

    public synchronized Set<String> getTarget(String src)
    {
        Set<String> set1 = mapSrc2Dst.get(src);
        if (ToolUtilities.isObjectEmpty(set1))
            return null;
        else
            return new TreeSet<String>(set1);
    }
    
    public Set<String> getAllTarget(String src)
    {
        Set<String> rs = new HashSet();
        getAllTarget(src, rs);
        return rs;
    }
    
    protected void getAllTarget(String src, Set<String> rs)
    {
        Set<String> lstTarget = getTarget_Unsafe(src);
        if (lstTarget != null)
            for (String target : lstTarget)
            {
                // 已经分析过了的就跳过
                if (rs.contains(target))
                    continue;
                
                rs.add(target);
                getAllTarget(target, rs);
            }
    }
    
    // 返回值可能为null，所以是unsafe
    public synchronized Set<String> getTarget_Unsafe(String src)
    {
        return mapSrc2Dst.get(src);
    }

    protected synchronized void clearDst2Src()
    {
        mapDst2Src.clear();
    }

    public synchronized void clearAll()
    {
        mapSrc2Dst.clear();
        mapDst2Src.clear();
    }

    protected synchronized void addDst2Src(String sTarget, Set<String> setSrc)
    {
        mapDst2Src.put(sTarget, setSrc);
    }

    protected synchronized void removeDst2Src(String dstRDN)
    {
        mapDst2Src.remove(dstRDN);
    }

    public synchronized boolean hasSource(String dst)
    {
        return !ToolUtilities.isObjectEmpty(mapDst2Src.get(dst));
    }

    public synchronized Set<String> getSource(String dst)
    {
        Set<String> set1 = mapDst2Src.get(dst);
        if (ToolUtilities.isObjectEmpty(set1))
            return null;
        else
            return new TreeSet<String>(set1);
    }
    
    public Set<String> getAllSource(String dst)
    {
        Set<String> rs = new HashSet();
        getAllSource(dst, rs);
        return rs;
    }
    
    protected void getAllSource(String dst, Set<String> rs)
    {
        Set<String> lstSrc = getSource_Unsafe(dst);
        if (lstSrc != null)
            for (String src : lstSrc)
            {
                // 已经分析过了的就跳过
                if (rs.contains(src))
                    continue;
                
                rs.add(src);
                getAllSource(src, rs);
            }
    }
    
    public synchronized Set<String> getSource_Unsafe(String dst)
    {
        return mapDst2Src.get(dst);
    }
    
    public synchronized int size()
    {
        return Math.max(mapSrc2Dst.size(), mapDst2Src.size());
    }
    
    public synchronized int getSourceCount(String dst)
    {
        Set<String> set1 = mapDst2Src.get(dst);
        if (ToolUtilities.isObjectEmpty(set1))
            return 0;
        else
            return set1.size();
    }
    
    public synchronized int getTargetCount(String src)
    {
        Set<String> set1 = mapSrc2Dst.get(src);
        if (ToolUtilities.isObjectEmpty(set1))
            return 0;
        else
            return set1.size();
    }

    public synchronized Map<String, Set<String>> getAllSource2Target()
    {
        return new HashMap(mapSrc2Dst);
    }

    public synchronized Map<String, Set<String>> getAllTarget2Source()
    {
        return new HashMap(mapDst2Src);
    }
    
    public synchronized Set<String> getSourceKeys()
    {
        return mapSrc2Dst.keySet();
    }
    
    public synchronized Set<String> getTargetKeys()
    {
        return mapDst2Src.keySet();
    }
    
    public synchronized long getTotalPipes()
    {
        long lTotalPipes = 0;
        for (Iterator it = mapSrc2Dst.values().iterator(); it.hasNext();)
        {
            Set set = (Set) it.next();
            if (set != null)
                lTotalPipes += set.size();
        }

        return lTotalPipes;
    }
    
    public synchronized boolean containsRelation(String src, String dst)
    {
        Set<String> setDst = mapSrc2Dst.get(src);
        if (ToolUtilities.isCollectionEmpty(setDst))
            return false;

        return setDst.contains(dst);
    }
    
    public boolean isDirRelation(String src, String dst)
    {
        if (src == null || dst == null)
            return false;

        if (ToolUtilities.isStringEqual(src, dst))
            return false;
        
        Set<String> sources = mapDst2Src.get(dst);
        if (sources == null)
            return false;

        return sources.contains(src);
    }
    
    // 判断srcNode是否dstNode的源节点，递归查询
    public boolean isSourceOfNode(String srcRDN, String dstRDN)
    {
        if (srcRDN == null || dstRDN == null)
            return false;

        if (ToolUtilities.isStringEqual(srcRDN, dstRDN))
            return false;
        
        Set<String> sources = mapDst2Src.get(dstRDN);
        if (ToolUtilities.isCollectionEmpty(sources))
            return false;

        if (sources.contains(srcRDN))
            return true;
        else
        {
            for (String src2src : sources)
            {
                if (isSourceOfNode(srcRDN, src2src))
                    return true;
            }

            return false;
        }
    }
    
    public List<NodePath<String>> getAllPathBetween(String src, String dst)
    {
        return getAllPathBetween(src, dst, null);
    }
    
    /**
     * 搜索src到dst之间的所有通路
     * @param filter : null或者返回true表示通过，返回false表示过滤跳过的意思
     */
    public List<NodePath<String>> getAllPathBetween(String src, String dst, BiFunction<String, String, Boolean> filter)
    {
        List<NodePath<String>> allLastPaths = new ArrayList<NodePath<String>>();
        NodePath firstPath = new NodePath();
        
        //这个源头不能加，加上虽然好看，但是在过滤时会把最短的那个给过滤掉
        firstPath.add(src);
        
        getAllBranchPathBetween(src, dst, allLastPaths, firstPath, filter);
        
        return allLastPaths;
    }
    
    protected synchronized void getAllBranchPathBetween(String src, String dst, List<NodePath<String>> allLastPaths, NodePath<String> currentPath)
    {
        getAllBranchPathBetween(src, dst, allLastPaths, currentPath, null);
    }
    
    //filter默认都是返回true
    protected synchronized void getAllBranchPathBetween(String src, String dst, List<NodePath<String>> allLastPaths, NodePath<String> currentPath, BiFunction<String, String, Boolean> filter)
    {
        Set<String> nextLayer = mapSrc2Dst.get(src);
        if (ToolUtilities.isCollectionEmpty(nextLayer))
            return;
        
        for (String nextLayerNode : nextLayer)
        {
            if (filter != null && !filter.apply(src, nextLayerNode))
                continue;
            
            if (currentPath.contains(nextLayerNode)) continue; // 避免循环
            
            NodePath newPath = (NodePath) currentPath.clone();
            
            newPath.add(nextLayerNode);
            if (nextLayerNode.equals(dst))
            {
                allLastPaths.add(newPath);
            } else
            {
                getAllBranchPathBetween(nextLayerNode, dst, allLastPaths, newPath, filter);
            }
        }
        return;
    }

    public synchronized void removeRelation(String src, String dst)
    {
        if (ToolUtilities.isStringEmpty(src) || ToolUtilities.isStringEmpty(dst))
            throw new RuntimeException("Found invalid relationship : " + src + " -> " + dst);

        // 从src的所有目标集合中去除dst，如果去除后src不再具有任何宿，则删掉src的目标集合
        Set<String> dstSet = mapSrc2Dst.get(src);
        if (!ToolUtilities.isObjectEmpty(dstSet))
        {
            dstSet.remove(dst);
            if (ToolUtilities.isObjectEmpty(dstSet))
                removeSrc2Dst(src);
        }

        // 再反过来再从dst的所有源中踢掉src
        Set<String> srcSet = mapDst2Src.get(dst);
        if (!ToolUtilities.isObjectEmpty(srcSet))
        {
            srcSet.remove(src);
            if (ToolUtilities.isObjectEmpty(srcSet))
                removeDst2Src(dst);
        }
    }

    public synchronized void removeOneNode(String nodeRDN)
    {
        // 遍历所有源端，一条一条的删除关系
        if (hasSource(nodeRDN))
        {
            Set<String> allSrc = getSource(nodeRDN);
            if (allSrc != null)
                for (String src : allSrc)
                    removeRelation(src, nodeRDN);
        }

        // 遍历所有宿端，一条一条的删除关系
        if (hasTarget(nodeRDN))
        {
            Set<String> allDst = getTarget(nodeRDN);
            if (allDst != null)
                for (String dst : allDst)
                    removeRelation(nodeRDN, dst);
        }

        // 最后，彻底清除自身的两端缓存
        removeSrc2Dst(nodeRDN);
        removeDst2Src(nodeRDN);
    }

    boolean _selfConnectable = false;
    
    
    public boolean isSelfConnectable()
    {
        return _selfConnectable;
    }

    public void setSelfConnectable(boolean _selfConnectable)
    {
        this._selfConnectable = _selfConnectable;
    }

    public synchronized void addAll(RelationMap otherMap)
    {
        for (String src : otherMap.getSourceKeys())
        {
            for (String dst : otherMap.getTarget(src))
                addRelation(src, dst);
        }
    }
    
    public synchronized void addRelation(String src, String dst)
    {
        if (ToolUtilities.isStringEmpty(src) || ToolUtilities.isStringEmpty(dst))
            throw new RuntimeException("Found invalid relationship : " + src + " -> " + dst );
        
        // 不允许自己跟自己连线
        if (ToolUtilities.isStringEqual(src, dst) && !isSelfConnectable())
            return;
        
        Set<String> setDst = mapSrc2Dst.get(src);
        if (setDst == null)
        {
            setDst = new TreeSet<String>();
            addSrc2Dst(src, setDst);
        }
        setDst.add(dst);

        Set<String> setSrc = mapDst2Src.get(dst);
        if (setSrc == null)
        {
            setSrc = new TreeSet<String>();
            addDst2Src(dst, setSrc);
        }
        setSrc.add(src);
    }
    
    // 允许自我连线，也允许加入目标为空
    public synchronized void addRelation_Unsafe(String src, String dst)
    {
        Set<String> setDst = mapSrc2Dst.get(src);
        if (setDst == null)
        {
            setDst = new TreeSet<String>();
            addSrc2Dst(src, setDst);
        }
        setDst.add(dst);

        Set<String> setSrc = mapDst2Src.get(dst);
        if (setSrc == null)
        {
            setSrc = new TreeSet<String>();
            addDst2Src(dst, setSrc);
        }
        setSrc.add(src);
    }
    
    public synchronized boolean isFirstLayer(String rdn)
    {
        return hasTarget(rdn) && !hasSource(rdn);
    }
    
    public synchronized boolean isLastLayer(String rdn)
    {
        return !hasTarget(rdn) && hasSource(rdn);
    }
    
    public synchronized boolean isAloneNode(String rdn)
    {
        return  !hasTarget(rdn) && !hasSource(rdn);
    }
    
    public Set<String> findHead()
    {
        Set<String> setHead = new HashSet();
        for (String src : mapSrc2Dst.keySet())
            if (isFirstLayer(src))
                setHead.add(src);
        
        return setHead;
                
    }
    
    public RelationMap clone()
    {
        RelationMap o = new RelationMap();
        o.mapSrc2Dst.putAll(mapSrc2Dst);
        o.mapDst2Src.putAll(mapDst2Src);
        
        return o;
    }
    

    protected  void getSourcePath(String root, String node, NodePath<String> path)
    {
        _getSourcePath(root, node, path, new HashSet());
    }
    
    protected  void _getSourcePath(String root, String node, NodePath<String> path, Set<String> visited)
    {
        if (visited.contains(node))
            return;
        
        path.add(0, node);
        visited.add(node);
        
        Set<String> lstSrc = getSource(node);
        if (lstSrc != null)
        {
            for (String src : lstSrc)
            {
                // 遇到循环到搜索起点，则忽略（很可能数据死递归了）
                if (CmnUtil.isStringEqual(root, src))
                    continue;
                
                // 移除自身，再把自身插到开头
                path.remove(src);
                _getSourcePath(root, src, path, visited);
            }
        }
    }
    
    public NodePath<String> getSourcePath(String node)
    {
        NodePath<String> path = new NodePath();
        getSourcePath(node, node, path);
        return path;
    }
    
    public void getAllTargetPaths(String src, RelationMap mapReturn, List<Pair<String, String>> lstCyclePath)
    {
        Set<String> lstDst = getTarget(src);
        if (lstDst != null)
            for (String dst : lstDst)
            {
                if (mapReturn.isSourceOfNode(dst, src))
                {
                    lstCyclePath.add(new Pair(src, dst));
                    continue;
                } else
                {
                    mapReturn.addRelation(src, dst);
                    getAllTargetPaths(dst, mapReturn, lstCyclePath);
                }
            }
    }
    
    public void getAllSourcePaths(String dst, RelationMap mapReturn, List<Pair<String, String>> lstCyclePath)
    {
        Set<String> lstSrc = getSource(dst);
        if (lstSrc != null)
            for (String src : lstSrc)
            {
                if (mapReturn.isSourceOfNode(dst, src))
                {
                    // 发现死循环
                    lstCyclePath.add(new Pair(src, dst));
                    continue;
                } else
                {
                    mapReturn.addRelation(src, dst);
                    getAllSourcePaths(src, mapReturn, lstCyclePath);
                }
            }
    }
    
    /**
     *  搜索某个节点前后所有路径，闭环回路存入lstCyclePath中
     * @param lstCyclePath : 输出型参数，造成闭环的回路会放入这里
     * 注：此函数乱序查找循环路径，不一定从头到尾那么顺畅，因为循环回路的甄别是随机的，回路里任何一段都有可能被识别为死循环回路
     */
    public RelationMap getAllPathOf(String node, List<Pair<String, String>> lstCyclePath)
    {
        RelationMap mapRet = new RelationMap();
        getAllSourcePaths(node, mapRet, lstCyclePath);
        RelationMap mapRet2 = new RelationMap();
        getAllTargetPaths(node, mapRet2, lstCyclePath);
        for (Entry<String, Set<String>> ent : mapRet2.getAllSource2Target().entrySet())
        {
            String src = ent.getKey();
            if (ent.getValue() != null)
                for (String dst : ent.getValue())
                {
                    if (mapRet.isSourceOfNode(dst, src))
                        lstCyclePath.add(new Pair(src, dst));
                    else
                        mapRet.addRelation(src, dst);
                }
        }
        
        for (Iterator<Pair<String, String>> it = lstCyclePath.iterator(); it.hasNext();)
        {
            Pair<String, String> cyclePair = it.next();
            if (mapRet.containsRelation(cyclePair.getKey(), cyclePair.getValue()))
                it.remove();
        }

        return mapRet;
    }
    
    /**
     *  获取从某个节点出发，前后所有相关关系，并挑出死循环回路
     *  FromHead的意思是，最后出来的链路关系以及循环回路的甄别，会自动从头层节点开始计算的
     *  会虚拟出头节点，进行二次计算，从而得到从头到尾比较顺的链路
     */
    public RelationMap getAllPathOf_SortFromHead(String node, List<Pair<String, String>> lstCyclePath)
    {
        RelationMap map1 = getAllPathOf(node, lstCyclePath);
        RelationMap map2 = map1.clone();
        for (Iterator<Pair<String, String>> it = lstCyclePath.iterator(); it.hasNext();)
        {
            Pair<String, String> cyclePair = it.next();
            map2.addRelation(cyclePair.left, cyclePair.right);
        }

        String virutalStart = ToolUtilities.allockUUID();
        Set<String> lstHead = map2.findHead();
        for (String h : lstHead)
        {
            map2.addRelation(virutalStart, h);
        }

        lstCyclePath.clear();
        RelationMap mapRet = map2.getAllPathOf(virutalStart, lstCyclePath);
        mapRet.removeOneNode(virutalStart);
        return mapRet;
    }

    public Set<String> searchStartNodes()
    {
        Set<String> setRet = new HashSet<String>();
        for (String src : mapSrc2Dst.keySet())
        {
            if (!hasSource(src))
                setRet.add(src);
        }
        
        return setRet;
    }

    public Set<String> searchEndNodes()
    {
        Set<String> setRet = new HashSet<String>();
        for (String dst : mapDst2Src.keySet())
        {
            if (!hasTarget(dst))
                setRet.add(dst);
        }
        
        return setRet;
    }
}
