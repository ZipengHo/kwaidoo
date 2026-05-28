package octo.cm.dto.flow;

import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cmn.util.JsonUtil;
import cmn.util.NullUtil;
import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.dfc.gui.LvUtil;
import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.dto.BusinessModelDto;
import octo.cm.enums.EnterRuleType;
import octo.cm.enums.NodeIOMappingType;
import octo.cm.flowengine.intf.BehaviorParam;
import octo.cm.flowengine.intf.FlowBehavior;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程编排
 *
 */
public class FlowDto extends BusinessModelDto implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = -4896002961485088196L;
	public final static String FormModelId = "octocm.md.flow.Flow";
	public final static String FieldCode_Name = "ming2Cheng1";
	public final static String sName = "名称";
	public final static String FieldCode_Description = "shuo1Ming2";
	public final static String sDescription = "说明";
	public final static String FieldCode_Node = "jie2Dian3";
	public final static String sNode = "节点";
	public final static String FieldCode_Links = "lian2Sian4";
	public final static String sLinks = "连线";
	public final static String FieldCode_Routers = "lu4You2";
	public final static String sRouters = "路由";
	public final static String FieldCode_Params = "can1Shu4Ding4Yi4";
	public final static String sParams = "参数定义";
	public final static String FieldCode_IoMappings = "jie2Dian3Shu1Ru4Shu1Chu1Ying4She4";
	public final static String sIoMappings = "节点输入输出映射";
	public final static String FieldCode_WfeFlowUuid = "liou2Cheng2Shih2Li4Wei2Yi1Biao1Shih1";
	public final static String sWfeFlowUuid = "流程实例唯一标识";
	public final static String FieldCode_FlowDefaultBehaviorIntf = "liou2Cheng2Mo4Ren4Sing2Wei2Jie1Kou3";
	public final static String sFlowDefaultBehaviorIntf = "流程默认行为接口";
	public final static String FieldCode_FlowDefaultBehaviorParams = "liou2Cheng2Mo4Ren4Sing2Wei2Can1Shu4";
	public final static String sFlowDefaultBehaviorParams = "流程默认行为参数";
	public final static String FieldCode_RuleNameSpace = "guei1Ze2Ming4Ming2Kong1Jian1";
	public final static String sRuleNameSpace = "规则命名空间";
	@FieldMeta(code = FieldCode_Name,name = sName, dataType = DataType.Text)
	String name;
	@FieldMeta(code = FieldCode_Description,name = sDescription, dataType = DataType.Text)
	String description;
	@FieldMeta(code = FieldCode_Node,name = sNode, dataType = DataType.NestingModel, tableModel = FlowNodeDto.class)
	List<FlowNodeDto> nodes;
	@FieldMeta(code = FieldCode_Links,name = sLinks, dataType = DataType.NestingModel, tableModel = NodeLinkDto.class)
	List<NodeLinkDto> links;
	@FieldMeta(code = FieldCode_Routers,name = sRouters, dataType = DataType.NestingModel, tableModel = FlowRouterDto.class)
	List<FlowRouterDto> routers;
	@FieldMeta(code = FieldCode_Params,name = sParams, dataType = DataType.NestingModel, tableModel = FlowParamDto.class)
	List<FlowParamDto> params;
	@FieldMeta(code = FieldCode_IoMappings,name = sIoMappings, dataType = DataType.NestingModel, tableModel = NodeIOMappingDto.class)
	List<NodeIOMappingDto> ioMappings;
	@FieldMeta(code = FieldCode_WfeFlowUuid,name = sWfeFlowUuid, dataType = DataType.Text)
	String wfeFlowUuid;
	@FieldMeta(code = FieldCode_FlowDefaultBehaviorIntf,name = sFlowDefaultBehaviorIntf, dataType = DataType.Text)
	String flowDefaultBehaviorIntf;
	@FieldMeta(code = FieldCode_FlowDefaultBehaviorParams,name = sFlowDefaultBehaviorParams, dataType = DataType.Text)
	String flowDefaultBehaviorParams;
	@FieldMeta(code = FieldCode_RuleNameSpace,name = sRuleNameSpace, dataType = DataType.Text)
	String ruleNameSpace;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<FlowNodeDto> getNodes() {
		return nodes;
	}

	public List<NodeLinkDto> getLinks() {
		return links;
	}

	public List<FlowRouterDto> getRouters() {
		return routers;
	}

	public FlowDto setName(String name) {
		this.name = name;
		return this;
	}

	public FlowDto setDescription(String description) {
		this.description = description;
		return this;
	}

	public FlowDto setNodes(List<FlowNodeDto> nodes) {
		this.nodes = nodes;
		return this;
	}

	public FlowDto setLinks(List<NodeLinkDto> links) {
		this.links = links;
		return this;
	}

	public FlowDto setRouters(List<FlowRouterDto> routers) {
		this.routers = routers;
		return this;
	}

//	public String getStartNode() {
//		return startNode;
//	}
//
//	public FlowDto setStartNode(String startNode) {
//		this.startNode = startNode;
//		return this;
//	}

	public List<FlowParamDto> getParams() {
		return params;
	}

	public FlowDto setParams(List<FlowParamDto> params) {
		this.params = params;
		return this;
	}
	
	public List<NodeIOMappingDto> getIoMappings() {
		return ioMappings;
	}

	public FlowDto setIoMappings(List<NodeIOMappingDto> ioMappings) {
		this.ioMappings = ioMappings;
		return this;
	}
	
	public String getWfeFlowUuid() {
		return wfeFlowUuid;
	}
	
	public FlowDto setWfeFlowUuid(String wfeFlowUuid) {
		this.wfeFlowUuid = wfeFlowUuid;
		return this;
	}
	
	public String getFlowDefaultBehaviorIntf() {
		return flowDefaultBehaviorIntf;
	}
	public FlowDto setFlowDefaultBehaviorIntf(String flowDefaultBehaviorIntf) {
		this.flowDefaultBehaviorIntf = flowDefaultBehaviorIntf;
		return this;
	}
	public String getFlowDefaultBehaviorParams() {
		return flowDefaultBehaviorParams;
	}
	public FlowDto setFlowDefaultBehaviorParams(String flowDefaultBehaviorParams) {
		this.flowDefaultBehaviorParams = flowDefaultBehaviorParams;
		return this;
	}

	public String getRuleNameSpace() {
		return ruleNameSpace;
	}

	public FlowDto setRuleNameSpace(String ruleNameSpace) {
		this.ruleNameSpace = ruleNameSpace;
		return this;
	}

	public String getWfeFlowName() {
		if (CmnUtil.isStringEmpty(getFormModelId())) {
			return FormModelId + "[" + getCode() + "]";
		} else {
			return getFormModelId() + "[" + getCode() + "]";
		}
	}

	public Map<String, EnterRuleType> getNodeEnterRules() {
		Map<String, EnterRuleType> nodeEnterRule = new LinkedHashMap<>();
		for (FlowRouterDto node : NullUtil.get(routers)) {
			for (EnterRouterDto enterRouter : NullUtil.get(node.getEnterRouters())) {
				EnterRuleType enterRule = enterRouter.getEnterRuleType();
				if (enterRule != null) {
					nodeEnterRule.put(node.getNode(), enterRule);
				}
			}
		}
		return nodeEnterRule;
	}

	public Map<String, FlowNodeDto> getNodeKeyMap() {
		Map<String, FlowNodeDto> nodeMap = new LinkedHashMap<>();
		for (FlowNodeDto node : NullUtil.get(nodes)) {
			nodeMap.put(node.getNodeKey(), node);
		}
		return nodeMap;
	}

	public Map<String, FlowNodeDto> getNodeNameMap() {
		Map<String, FlowNodeDto> nodeMap = new LinkedHashMap<>();
		for (FlowNodeDto node : NullUtil.get(nodes)) {
			nodeMap.put(node.getName(), node);
		}
		return nodeMap;
	}

	public List<NodeIOMappingDto> getNodeIOMappingParams(String nodeName, NodeIOMappingType ioType) {
		return NullUtil.get(ioMappings).stream()
				.filter(v -> CmnUtil.isStringEqual(v.getNodeName(), nodeName) && v.getMappingTypeEnum() == ioType)
				.collect(Collectors.toList());
	}
	
	public Class<? extends FlowBehavior> getFlowDefaultBehaviorClass(){
		if (CmnUtil.isStringEmpty(flowDefaultBehaviorIntf))
			return null;
		try {
			return ClassFactory.loadClass(flowDefaultBehaviorIntf);
		}catch (Exception e) {
			LvUtil.trace("流程默认行为接口不存在！"+ flowDefaultBehaviorIntf);
			return null;
		}
	}
	
	public FlowDto setFlowDefaultBehaviorIntf(Class<? extends FlowBehavior> flowDefaultBehaviorIntf) {
		this.flowDefaultBehaviorIntf = flowDefaultBehaviorIntf.getName();
		return this;
	}
	public FlowDto setFlowDefaultBehaviorParams(BehaviorParam params) {
		if(params != null)
			this.flowDefaultBehaviorParams = JsonUtil.toJson(params);
		return this;
	}
	public <T extends BehaviorParam> T getFlowDefaultBehaviorParams(Class<T> clazz) {
		if(CmnUtil.isStringEmpty(flowDefaultBehaviorParams))
			return null;
		try(IJson json = IJsonService.get().getJson()){
			T param = json.fromJson(flowDefaultBehaviorParams,clazz);
			return param;
		}
	}

	public List<String> getRuleNameSpaces(){
		if(CmnUtil.isStringEmpty(ruleNameSpace))
			return Collections.emptyList();
		return Arrays.asList(ruleNameSpace.split(","));
	}

}
