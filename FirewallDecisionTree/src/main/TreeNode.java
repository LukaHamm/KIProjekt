package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {

	private List<TreeNode> treenodeList;
	private Map<String,Integer> branchingMap;
	private Attribute attribute;
	
	
	
	public TreeNode(Map<String, Integer> branchingMap, Attribute attribute) {
		this.treenodeList = new ArrayList<>();
		this.attribute = attribute;
	}
	
	public TreeNode(Attribute attribute) {
		this.treenodeList = new ArrayList<>();
		this.branchingMap = new HashMap<>();
		this.attribute = attribute;
	}



	public String evaluate (String evaluateString) {
		String attributeState =  attribute.evaluate(evaluateString);
		if(treenodeList == null) {
			return attributeState;
		}
		TreeNode nextTreeNode = treenodeList.get(branchingMap.get(attributeState));
		return nextTreeNode.evaluate(evaluateString);
	}

	public List<TreeNode> getTreenodeList() {
		return treenodeList;
	}

	public Map<String, Integer> getBranchingMap() {
		return branchingMap;
	}
	
	
	
	
	
}
