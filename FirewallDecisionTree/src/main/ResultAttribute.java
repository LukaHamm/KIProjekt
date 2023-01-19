package main;

import java.util.List;

public class ResultAttribute extends Attribute {


	public ResultAttribute(Attribute branchAttributeToCopy) {
		super(branchAttributeToCopy);
	}

	public ResultAttribute(String attributeName) {
		super(attributeName);
	}

	/**
	 * Gibt den Endwert aus 
	 */
	@Override
	public String evaluate(String AttributeToEvaluate) {
		return attributeValue;
	}

	
}
