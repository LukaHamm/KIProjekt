package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static ArrayList<Attribute> attributeList;

	public static void main(String[] args) throws FileNotFoundException {
		for (;;) {
			Scanner input = new Scanner(System.in);
			System.out.println("Bitte geben Sie den Pfad für die Attributkonfiguration ein:");
			String pathAttributeConfig = input.nextLine();
			System.out.println("Bitte geben Sie den Dateinamen ein:");
			String filenameAttributeConfig = input.nextLine();
			System.out.println("Bitte geben Sie den Pfad für die Attributwertkombinationen ein:");
			String pathAttributeWeightConfig = input.nextLine();
			System.out.println("Bitte geben Sie den Dateinamen ein:");
			String filenameAttributeWeight = input.nextLine();
			TreeBuilder treeBuilder = new TreeBuilder();
			try {
				TreeNode treeNode = treeBuilder.build(pathAttributeConfig, filenameAttributeConfig,
						pathAttributeWeightConfig, filenameAttributeWeight);
				System.out.println("Baum erstellt!");
				StringBuilder builder = new StringBuilder();
				treeNode.print(builder, "", "");
				System.out.println(builder);
				try (FileWriter fw = new FileWriter(
						new File(pathAttributeConfig + "/" + "tree.txt"),
						StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(fw)) {
					writer.write(builder.toString());
				} catch (IOException e) {
					System.out.println("Beim Speichern der Baumstruktur als Textdatei ist ein Fehler aufgetreten");
					continue;
				}
				System.out.println("Geben Sie eine Zeichenkette ein die geprüft werden soll");
				String testString = input.nextLine();
				String outcome = treeNode.evaluate(testString);
				System.out.println("Ergebnis der Prüfung: " + outcome);

			} catch (FileNotFoundException e) {
				System.out.println("Die Datei konnte unter dem angegebenen Pfad nicht gefunden werden");
				continue;
			} catch (AttributeNoMatchException e1) {
				System.out.println(e1.getMessage());
				continue;
			}
			System.out.println("Möchten Sie weitere Entscheidungsbäume erstellen (J) oder beenden (N)?");
			String confirmation = input.nextLine();
			confirmation = confirmation.toUpperCase();
			if (confirmation.equals("N")) {
				break;
			}
		}
	}

}
