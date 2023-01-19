package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {

	private List<TreeNode> treenodeList;
	private Map<String, Integer> branchingMap;
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

	/**
	 * wertet einen uebergebenen String mit Attributwertkombinationen aus
	 * @param evaluateString
	 * @return
	 * @throws AttributeNoMatchException Wenn bei einem Attribut kein Attributwert zutrifft
	 */
	public String evaluate(String evaluateString) throws AttributeNoMatchException {
		String attributeState = attribute.evaluate(evaluateString);
		if (treenodeList.isEmpty()) {
			return attributeState;
		}
		TreeNode nextTreeNode = treenodeList.get(branchingMap.get(attributeState));
		return nextTreeNode.evaluate(evaluateString);
	}

	/**
	 * gibt den Baum textuell in Baumstruktur aus
	 * @param builder der Baum-String wird in den uebergebenen String-Builder uebertragen
	 * @param prefix
	 * @param childrenPrefix
	 */
	public void print(StringBuilder builder, String prefix, String childrenPrefix) {
		builder.append(prefix);
		builder.append(attribute.getAttributeName());
		StringBuilder paddingBuilder = new StringBuilder();
		if (treenodeList.isEmpty()) {
			builder.append("──" + attribute.getAttributeValue());
		} else {
			int intendation = prefix.length();
			if (prefix.isEmpty() || prefix.contains("└──")) {
				for (int i = 0; i < intendation; i++) {
					paddingBuilder.append(" ");
				}
			} else {
				paddingBuilder.append("|");
				for (int i = 0; i < intendation; i++) {
					paddingBuilder.append(" ");
				}
			}
		}
		builder.append('\n');
		for (int i = 0; i < treenodeList.size(); i++) {
			String attributeValue = attribute.getAttributeValueOptions().get(i);
			if (i != attribute.getAttributeValueOptions().size() - 1 && !treenodeList.isEmpty()) {
				String nextNode = paddingBuilder.toString() + "├──" + attributeValue + "──";
				treenodeList.get(branchingMap.get(attributeValue)).print(builder, nextNode, paddingBuilder.toString());
			} else {
				String nextNode = paddingBuilder.toString() + "└──" + attributeValue + "──";
				treenodeList.get(branchingMap.get(attributeValue)).print(builder, nextNode, paddingBuilder.toString());
			}
		}
	}

	public List<TreeNode> getTreenodeList() {
		return treenodeList;
	}

	public Map<String, Integer> getBranchingMap() {
		return branchingMap;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

}
