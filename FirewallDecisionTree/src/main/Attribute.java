package main;

import java.util.ArrayList;
import java.util.List;

public abstract class Attribute {

	protected String attributeValue;
	protected String attributeName;
	protected List<String> attributeValueOptions;
	
	public Attribute(String attributeName) {
		this.attributeName = attributeName;
		this.attributeValueOptions = new ArrayList<>();
	}
	
	public Attribute(Attribute branchAttributeToCopy) {
		this.attributeName = branchAttributeToCopy.getAttributeName();
		this.attributeValueOptions = branchAttributeToCopy.getAttributeValueOptions();
	}
	
	public abstract String evaluate (String AttributeToEvaluate);

	public List<String> getAttributeValueOptions() {
		return attributeValueOptions;
	}

	public void setAttributeValueOptions(List<String> attributeValueOptions) {
		this.attributeValueOptions = attributeValueOptions;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	

}
