package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class TreeBuilder {

	private TreeNode treeNode;
	private List<Attribute> attributeList;
	private List<List<Attribute>> attributeWeightList;
	private Map<Integer, String> gainToResultAttributeValueMap;

	public TreeNode build(String pathToAttributeConfig, String pathToAttributeWeight) throws FileNotFoundException {
		buildAttributeList(pathToAttributeConfig);
		gainToResultAttributeValueMap = new HashMap<>();
		int gainKey = 0;
		for (String val : attributeList.get(attributeList.size() - 1).getAttributeValueOptions()) {
			gainKey--;
			gainToResultAttributeValueMap.put(gainKey, val);
		}
		readAttributeWeight(pathToAttributeWeight);
		buildTreeRoot();
		buildTree(null, treeNode);
		return treeNode;
	}

	private void readAttributeWeight(String pathToAttributeWeight) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(pathToAttributeWeight));
		attributeWeightList = new ArrayList<>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner rowScanner = new Scanner(line);
			rowScanner.useDelimiter("\\|");
			List<Attribute> attributeCombinationList = new ArrayList<>();
			int rowIndex = 0;
			while (rowScanner.hasNext()) {
				if (rowIndex == attributeList.size() - 1) {
					ResultAttribute resultAttribute = new ResultAttribute(attributeList.get(rowIndex));
					resultAttribute.setAttributeValue(rowScanner.next());
					attributeCombinationList.add(resultAttribute);
				} else {
					BranchAttribute branchAttribute = new BranchAttribute(attributeList.get(rowIndex));
					branchAttribute.setAttributeValue(rowScanner.next());
					attributeCombinationList.add(branchAttribute);
				}
				rowIndex++;
			}
			attributeWeightList.add(attributeCombinationList);
		}
		scanner.close();
	}

	private void buildTreeRoot() {
		double entropyTotal = calculateEntropyTotal((ResultAttribute) attributeList.get(attributeList.size() - 1),
				null);
		double maxGain = 0;
		Attribute attributeMaxGain = null;
		for (Attribute attribute : attributeList) {
			if (!(attribute instanceof ResultAttribute)) {
				double gain = calculateGain(entropyTotal, attribute, null);
				if (gain > maxGain) {
					maxGain = gain;
					attributeMaxGain = attribute;
				}
			}
		}
		treeNode = new TreeNode(new BranchAttribute(attributeMaxGain));
		attributeList.remove(attributeMaxGain);
//		int index = attributeList.indexOf(attributeMaxGain);
//		attributeList.remove(attributeMaxGain);
//		List<Attribute> childAttributesList = new ArrayList<>();
//		List<String> attributeValueListMaxGain = attributeMaxGain.getAttributeValueOptions();
//		for(String attributeValue:attributeValueListMaxGain) {
//			double maxGainChildAttribute = 0;
//			Attribute maxChildAttribute = null;
//			for(Attribute attribute : attributeList) {
//				double gain = calculateGain(attribute, entropyTotal, index, attributeValue);
//				if(gain > maxGainChildAttribute) {
//					maxGainChildAttribute = gain;
//					maxChildAttribute = attribute;
//				}
//			}
//			childAttributesList.add(maxChildAttribute);
//		}
//		for(int i = 0; i<childAttributesList.size();i++) {
//			treeNode.getTreenodeList().add(new TreeNode(childAttributesList.get(i)));
//			treeNode.getBranchingMap().put(attributeValueListMaxGain.get(i), i);
//		}
	}

	private double calculateEntropyTotal(ResultAttribute resultAttribute, List<Attribute> atrributeCombinations) {
		Map<String, Integer> attributeApperanceCount = new HashMap<>();
		for (String attrValue : resultAttribute.getAttributeValueOptions()) {
			int attributeValueCount = 0;
			for (List<Attribute> attributeList : attributeWeightList) {
				boolean skipRow = false;
				if (atrributeCombinations != null) {
					for (Attribute attributeCombination : atrributeCombinations) {
						Attribute attributeWeight = attributeList.stream()
								.filter(a -> a.getAttributeName().equals(attributeCombination.getAttributeName()))
								.findAny().get();
						if (!attributeCombination.getAttributeValue().equals(attributeWeight.getAttributeValue())) {
							skipRow = true;
							break;
						}
					}
				}
				if (skipRow) {
					continue;
				}
				Attribute attr = attributeList.get(attributeList.size() - 1);
				if (attr.getAttributeValue().equals(attrValue)) {
					attributeValueCount++;
				}
			}

			attributeApperanceCount.put(attrValue, attributeValueCount);
		}
		int attributapperanceTotal = atrributeCombinations != null
				? calculateAttributeAppearanceCountTotal(atrributeCombinations)
				: attributeWeightList.size();
		double entropy = 0;
		for (Entry<String, Integer> entry : attributeApperanceCount.entrySet()) {
			entropy = entropy + Math.abs(log2((double) entry.getValue() / attributapperanceTotal)
					* ((double) entry.getValue() / attributapperanceTotal));
		}
		return entropy;
	}

	/*
	 * -Abfrage gain von null -Es muss moeglich sein mehrere
	 * Attributwertkombinationen zu ignorieren - in einer Ebene suchen - zur root
	 * zurückspringen bis in entspr. Ebene gehen
	 */
	private double calculateGain(double entropyTotal, Attribute attr, List<Attribute> attributeValueCombinations) {
		Map<String, Map<String, Integer>> attributeApperanceCount = new HashMap<>();
		int attributeApperanceTotal = attributeValueCombinations == null ? attributeWeightList.size()
				: calculateAttributeAppearanceCountTotal(attributeValueCombinations);
		for (String attributeValue : attr.getAttributeValueOptions()) {
			attributeApperanceCount.put(attributeValue, new HashMap<>());
			for (List<Attribute> attributeList : attributeWeightList) {
				boolean skipRow = false;
				if (attributeValueCombinations != null) {
					for (Attribute attributeCombination : attributeValueCombinations) {
						Attribute attributeWeight = attributeList.stream()
								.filter(a -> a.getAttributeName().equals(attributeCombination.getAttributeName()))
								.findAny().get();
						if (!attributeCombination.getAttributeValue().equals(attributeWeight.getAttributeValue())) {
							skipRow = true;
							break;
						}
					}
				}
				if (skipRow) {
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
		double entropyAttribute = 0;
		List<List<Double>> attrValueCountEntropie = new ArrayList<>();
		for (Entry<String, Map<String, Integer>> entry : attributeApperanceCount.entrySet()) {
			double entropyAttributeValue = 0;
			int attributeValueApperanceCountTotal = entry.getValue().entrySet().stream()
					.map(Entry<String, Integer>::getValue).mapToInt(Integer::intValue).sum();
			for (Entry<String, Integer> entryOutcomeVal : entry.getValue().entrySet()) {
				double quotientAttributeAttributeVallue = (double) entryOutcomeVal.getValue()
						/ attributeValueApperanceCountTotal;
				entropyAttributeValue = entropyAttributeValue
						+ Math.abs((double) log2(quotientAttributeAttributeVallue) * quotientAttributeAttributeVallue);

			}
			attrValueCountEntropie.add(
					Arrays.asList(new Double[] { (double) attributeValueApperanceCountTotal, entropyAttributeValue }));
		}
		for (List<Double> attrVal : attrValueCountEntropie) {
			entropyAttribute = entropyAttribute + (attrVal.get(0) / attributeApperanceTotal * attrVal.get(1));
		}
		if (entropyAttribute == 0) {
			List<String> outComeValueList = new ArrayList<>();
			boolean isOnlyOneOutcome = true;
			for (Entry<String, Map<String, Integer>> attrEntry : attributeApperanceCount.entrySet()) {
				for (Entry<String, Integer> outcomeEntry : attrEntry.getValue().entrySet()) {
					if (!outComeValueList.isEmpty()) {
						if (!outComeValueList.contains(outcomeEntry.getKey())) {
							isOnlyOneOutcome = false;
							break;
						}
					} else {
						outComeValueList.add(outcomeEntry.getKey());
					}
				}
				if (!isOnlyOneOutcome) {
					break;
				}
			}
			if (isOnlyOneOutcome) {
				int gain = gainToResultAttributeValueMap.entrySet().stream()
						.filter(e -> e.getValue().equals(outComeValueList.get(0))).findAny().get().getKey();
				return gain;
			}
		}

		return entropyTotal - entropyAttribute;

	}

	private int calculateAttributeAppearanceCountTotal(List<Attribute> attributeValueCombinations) {
		int apperanceCount = 0;
		for (List<Attribute> attrList : attributeWeightList) {
			boolean skipRow = false;
			for (Attribute attributeCombination : attributeValueCombinations) {
				Attribute attributeWeight = attrList.stream()
						.filter(a -> a.getAttributeName().equals(attributeCombination.getAttributeName())).findAny()
						.get();
				if (!attributeCombination.getAttributeValue().equals(attributeWeight.getAttributeValue())) {
					skipRow = true;
					break;
				}
			}
			if (skipRow) {
				continue;
			}
			apperanceCount++;
		}
		return apperanceCount;
	}

	private double log2(double value) {
		return Math.log(value) / Math.log(2.0);
	}

	private void buildAttributeList(String pathToAttributeConfig) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(pathToAttributeConfig));
		Scanner rowScanner = new Scanner(scanner.nextLine());
		rowScanner.useDelimiter("\\|");
		attributeList = new ArrayList<>();
		while (rowScanner.hasNext()) {
			String attributeName = rowScanner.next();
			if (rowScanner.hasNext()) {
				attributeList.add(new BranchAttribute(attributeName));
			} else {
				attributeList.add(new ResultAttribute(attributeName));
			}
		}
		while (scanner.hasNextLine()) {
			int rowIndex = 0;
			String line = scanner.nextLine();
			rowScanner = new Scanner(line);
			rowScanner.useDelimiter("\\|");
			while (rowScanner.hasNext()) {
				String attributeValue = rowScanner.next();
				if (!attributeValue.equals("none")) {
					attributeList.get(rowIndex).getAttributeValueOptions().add(attributeValue);
					rowIndex++;
				}
			}
		}
		scanner.close();
		rowScanner.close();
	}

	// Entropie erneut ausrechnen
	private void buildTree(List<Attribute> attributeValueCombinations, TreeNode treeNode) {
		Attribute attributeValuePathTaken = new BranchAttribute(treeNode.getAttribute());
		if (attributeValueCombinations == null) {
			attributeValueCombinations = List.of(attributeValuePathTaken);
		} else {
			attributeValueCombinations.add(attributeValuePathTaken);
		}
		int indexBranchMapping = 0;
		for (String attributeValue : treeNode.getAttribute().getAttributeValueOptions()) {
			double maxGain = 0;
			Attribute attributeMaxGain = null;
			attributeValuePathTaken.setAttributeValue(attributeValue);
			double entropy = calculateEntropyTotal((ResultAttribute) attributeList.get(attributeList.size() - 1),
					attributeValueCombinations);
			for (Attribute attribute : attributeList) {
				if (!(attribute instanceof ResultAttribute)) {
					double gain = calculateGain(entropy, attribute, attributeValueCombinations);
					if (gain < 0) {
						maxGain = gain;
						attributeMaxGain = new ResultAttribute(attributeList.get(attributeList.size() - 1));
						break;
					}
					if (gain > maxGain) {
						maxGain = gain;
						attributeMaxGain = attribute;
					}
				}
			}
			if (maxGain > 0) {
				List<Attribute> attributeValueCombinationsAttributeAdded = new ArrayList<>(attributeValueCombinations);
				TreeNode childTreeNode = new TreeNode(new BranchAttribute(attributeMaxGain));
				treeNode.getBranchingMap().put(attributeValue, indexBranchMapping);
				treeNode.getTreenodeList().add(childTreeNode);
				attributeList.remove(attributeMaxGain);
				buildTree(attributeValueCombinationsAttributeAdded, childTreeNode);
			} else {
				TreeNode childTreeNode = new TreeNode(new ResultAttribute(attributeMaxGain));
				childTreeNode.getAttribute().setAttributeValue(gainToResultAttributeValueMap.get((int) maxGain));
				treeNode.getBranchingMap().put(attributeValue, indexBranchMapping);
				treeNode.getTreenodeList().add(childTreeNode);
				attributeList.remove(attributeMaxGain);
			}
			indexBranchMapping++;
		}
	}
}
