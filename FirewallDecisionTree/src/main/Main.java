package main;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {
		String pathAttributeConfig = Main.class.getResource("attributeConfig.txt").getFile();
		String pathAttributeWeightConfig = Main.class.getResource("attributeGewichtung.txt").getFile();
		TreeBuilder treeBuilder = new TreeBuilder();
		try {
			TreeNode treeNode =  treeBuilder.build(pathAttributeConfig, pathAttributeWeightConfig);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
