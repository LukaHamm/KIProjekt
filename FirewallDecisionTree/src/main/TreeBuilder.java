package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TreeBuilder {

	private TreeNode treeNode;
	private List<Attribute> attributeList;
	private List<List<Attribute>> attributeWeightList;

	public TreeNode build(String pathToAttributeConfig, String pathToAttributeWeight) {
		buildAttributeList(pathToAttributeConfig);
		readAttributeWeight(pathToAttributeWeight);
		buildTreeRoot();
		buildTree();
		return treeNode;
	}

	private void readAttributeWeight(String pathToAttributeWeight) {
		Scanner scanner = new Scanner(pathToAttributeWeight);
		while (scanner.hasNextLine()) {
			Scanner rowScanner = new Scanner(scanner.next());
			rowScanner.useDelimiter(",");
			List<Attribute> attributeCombinationList = new ArrayList<>();
			int rowIndex = 0;
			while (rowScanner.hasNext()) {
				if (rowIndex == attributeList.size() - 1) {
					ResultAttribute resultAttribute = new ResultAttribute(attributeList.get(rowIndex));
					resultAttribute.setAttributeValue(rowScanner.next());
					attributeCombinationList.add(resultAttribute);
				} else {
					BranchAttribute branchAttribute = new BranchAttribute(attributeList.get(rowIndex));
					branchAttribute.setAttributeValue(scanner.next());
					attributeCombinationList.add(branchAttribute);
				}
				attributeWeightList.add(attributeCombinationList);
			}

		}
		scanner.close();
	}

	private void buildTreeRoot() {
		double entropyTotal = calculateEntropyTotal((ResultAttribute) attributeList.get(attributeList.size() - 1));
		double maxGain = 0;
		Attribute attributeMaxGain = null;
		for(Attribute attribute:attributeList) {
			double gain = calculateGain(attribute, entropyTotal, 0, null);
			if(gain > maxGain) {
				maxGain = gain;
				attributeMaxGain = attribute;
			}
		}
		treeNode = new TreeNode(new BranchAttribute(attributeMaxGain));
		int index = attributeList.indexOf(attributeMaxGain);
		attributeList.remove(attributeMaxGain);
		List<Attribute> childAttributesList = new ArrayList<>();
		List<String> attributeValueListMaxGain = attributeMaxGain.getAttributeValueOptions();
		for(String attributeValue:attributeValueListMaxGain) {
			double maxGainChildAttribute = 0;
			Attribute maxChildAttribute = null;
			for(Attribute attribute : attributeList) {
				double gain = calculateGain(attribute, entropyTotal, index, attributeValue);
				if(gain > maxGainChildAttribute) {
					maxGainChildAttribute = gain;
					maxChildAttribute = attribute;
				}
			}
			childAttributesList.add(maxChildAttribute);
		}
		for(int i = 0; i<childAttributesList.size();i++) {
			treeNode.getTreenodeList().add(new TreeNode(childAttributesList.get(i)));
			treeNode.getBranchingMap().put(attributeValueListMaxGain.get(i), i);
		}
	}

	private double calculateEntropyTotal(ResultAttribute resultAttribute) {
		Map<String, Integer> attributeApperanceCount = new HashMap<>();
		for (String attrValue : resultAttribute.getAttributeValueOptions()) {
			int attributeValueCount = 0;
			for (List<Attribute> attributeList : attributeWeightList) {
				Attribute attr = attributeList.get(attributeList.size() - 1);
				if (attr.getAttributeValue().equals(attrValue)) {
					attributeValueCount++;
				}
			}
			attributeApperanceCount.put(attrValue, attributeValueCount);
		}
		int attributapperanceTotal = attributeWeightList.size();
		double entropy = 0;
		for (Entry<String, Integer> entry : attributeApperanceCount.entrySet()) {
			entropy = entropy + Math.abs(log2((double) entry.getValue() / attributapperanceTotal)
					* (entry.getValue() / attributapperanceTotal));
		}
		return entropy;
	}

	/*  -Abfrage gain von null
	 *  -Es muss moeglich sein mehrere Attributwertkombinationen zu ignorieren
	 *  - in einer Ebene suchen
	 *  - zur root zurückspringen bis in entspr. Ebene gehen
	 */
	private double calculateGain(Attribute attr, double entropyTotal, int attributeIndexToSkip ,String attributeValueToTest) {
		Map<String, Map<String, Integer>> attributeApperanceCount = new HashMap<>();
		for (String attributeValue : attr.getAttributeValueOptions()) {
			for (List<Attribute> attributeList : attributeWeightList) {
				if(attributeValueToTest != null && attributeList.get(attributeIndexToSkip).getAttributeValue().equals(attributeValueToTest)) {
					continue;
				}
				for (Attribute attributeWeight : attributeList) {
					if (attributeWeight.getAttributeName().equals(attr.getAttributeName())) {
						if (attributeValue.equals(attributeWeight.getAttributeValue())) {
							String outcomeValue = attributeList.get(attributeList.size() - 1).getAttributeValue();
							Integer count = attributeApperanceCount.get(attributeValue).get(outcomeValue);
							if (count != null) {
								count++;
								attributeApperanceCount.get(attributeValue).put(outcomeValue, count);
							} else {
								attributeApperanceCount.get(attributeValue).put(outcomeValue, 1);
							}

						}
					}
				}
			}
		}
		int attributeApperanceTotal = attributeWeightList.size();
		double entropyAttribute = 0;
		List<List<Double>> attrValueCountEntropie = new ArrayList<>();
		for (Entry<String, Map<String, Integer>> entry : attributeApperanceCount.entrySet()) {
			double entropyAttributeValue = 0;
			int attributeValueApperanceCountTotal = entry.getValue().entrySet().stream()
					.map(Entry<String, Integer>::getValue).mapToInt(Integer::intValue).sum();
			for (Entry<String, Integer> entryOutcomeVal : entry.getValue().entrySet()) {
				entropyAttributeValue = entropyAttributeValue
						+ Math.abs((double) log2(entryOutcomeVal.getValue() / attributeValueApperanceCountTotal)
								* entryOutcomeVal.getValue() / attributeValueApperanceCountTotal);

			}
			attrValueCountEntropie.add(
					Arrays.asList(new Double[] { (double) attributeValueApperanceCountTotal, entropyAttributeValue }));
		}
		for (List<Double> attrVal : attrValueCountEntropie) {
			entropyAttribute = entropyAttribute + (attrVal.get(0) / attributeApperanceTotal * attrVal.get(1));
		}
		return entropyTotal - entropyAttribute;

	}

	private double log2(double value) {
		return Math.log(value) / Math.log(2.0);
	}

	private void buildAttributeList(String pathToAttributeConfig) {
		Scanner scanner = new Scanner(pathToAttributeConfig);
		Scanner rowScanner = new Scanner(scanner.nextLine());
		rowScanner.useDelimiter(",");
		int rowIndex = 0;
		while (rowScanner.hasNext()) {
			String attributeName = rowScanner.next();
			if (rowScanner.hasNext()) {
				attributeList.add(new BranchAttribute(attributeName));
			} else {
				attributeList.add(new ResultAttribute(attributeName));
			}
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			rowScanner = new Scanner(line);
			rowScanner.useDelimiter(",");
			while (rowScanner.hasNext()) {
				attributeList.get(rowIndex).getAttributeValueOptions().add(rowScanner.next());
				rowIndex++;
			}
		}
		scanner.close();
		rowScanner.close();
	}

	private void buildTree() {
		for(TreeNode treeNode: this.treeNode.getTreenodeList()) {
			
		}
	}
}
