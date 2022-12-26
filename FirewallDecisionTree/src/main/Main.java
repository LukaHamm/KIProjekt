package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static ArrayList<Attribute> attributeList;

	public static void main(String[] args) throws FileNotFoundException {
		String pathAttributeConfig = Main.class.getResource("attributeConfig2.txt").getFile();
		String pathAttributeWeightConfig = Main.class.getResource("attributeGewichtung2.txt").getFile();
//		List<String> rows = new ArrayList<>();
//		buildAttributeList(pathAttributeConfig);
//		int counter = 0;
//		for (Attribute attr : attributeList) {
//			if (!rows.isEmpty()) {
//				List<String> newRowList = new ArrayList<>();
//				for (String row : rows) {
//					if (!(attr instanceof ResultAttribute)) {
//						String copiedRow = new String(row);
//						row = row + "|" + attr.getAttributeValueOptions().get(0);
//						copiedRow = copiedRow + "|" + attr.getAttributeValueOptions().get(1);
//						newRowList.add(row);
//						newRowList.add(copiedRow);
//					} else {
//						row = row + "|" + attr.getAttributeValueOptions().get(0);
//						newRowList.add(row);
//						counter++;
//						System.out.println(row);
//					}
//				}
//				rows = newRowList;
//			} else {
//				rows.add(attr.getAttributeValueOptions().get(0));
//				rows.add(attr.getAttributeValueOptions().get(1));
//			}
//			System.out.println(counter);
//		}
//		try {
//			FileWriter writer = new FileWriter(pathAttributeWeightConfig);
//			for (String row : rows) {
//				writer.write(row + System.getProperty("line.separator"));
//			}
//
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		TreeBuilder treeBuilder = new TreeBuilder();
		try {
			TreeNode treeNode = treeBuilder.build(pathAttributeConfig, pathAttributeWeightConfig);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void buildAttributeList(String pathToAttributeConfig) throws FileNotFoundException {
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

}
