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
	
	
	@Override
	public String evaluate(String AttributeToEvaluate) {
		for(String attributeValueOption: attributeValueOptions) {
			pattern = Pattern.compile(attributeValueOption);
			matcher = pattern.matcher(AttributeToEvaluate);
			if(matcher.find()) {
				return attributeValueOption;
			}
		}
		return null;
	}

}
