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

    public String evaluate(String evaluateString) {
        String attributeState = attribute.evaluate(evaluateString);
        if (treenodeList.isEmpty()) {
            return attributeState;
        }
        TreeNode nextTreeNode = treenodeList.get(branchingMap.get(attributeState));
        return nextTreeNode.evaluate(evaluateString);
    }

    public void print(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(attribute.getAttributeName());
        StringBuilder paddingBuilder = new StringBuilder(childrenPrefix);
        if (treenodeList.isEmpty()) {
            builder.append("──" + attribute.getAttributeValue());
        }else {
            if (prefix.isEmpty()){
                paddingBuilder.append(" ");
            }else {
                paddingBuilder.append("| ");
            }
        }
        builder.append('\n');
        for (int i = 0; i < treenodeList.size(); i++) {
            String attributeValue = attribute.getAttributeValueOptions().get(i);
            if (i != attribute.getAttributeValueOptions().size() - 1 && !treenodeList.isEmpty()) {
                String nextNode = paddingBuilder.toString() + "├──" + attributeValue + "──";
                treenodeList.get(branchingMap.get(attributeValue))
                    .print(builder, nextNode,  paddingBuilder.toString());
            } else {
                String nextNode = paddingBuilder.toString() + "└── " + attributeValue + "──";
                treenodeList.get(branchingMap.get(attributeValue))
                    .print(builder, nextNode, paddingBuilder.toString());
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
