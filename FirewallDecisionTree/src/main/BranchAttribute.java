package main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BranchAttribute extends Attribute {

	public BranchAttribute(Attribute branchAttributeToCopy) {
		super(branchAttributeToCopy);
	}


	public BranchAttribute(String attributeName) {
		super(attributeName);
	}


	private Pattern pattern;
	private Matcher matcher;
	
	/**
	 * Wertet den String mit uebergebenen Attributwerten aus
	 * @throws AttributeNoMatchException Wenn bei einem Attribut kein Attributwert zutrifft
	 */
	@Override
	public String evaluate(String AttributeToEvaluate) throws AttributeNoMatchException {
		for(String attributeValueOption: attributeValueOptions) {
			pattern = Pattern.compile(attributeValueOption);
			matcher = pattern.matcher(AttributeToEvaluate);
			if(matcher.find()) {
				return attributeValueOption;
			}
		}
		throw new AttributeNoMatchException("Kein Attributwert kann in uebergebenen String ermittelt werden");
	}

}
