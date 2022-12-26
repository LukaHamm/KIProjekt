package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(attributeName, attributeValue, attributeValueOptions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		return Objects.equals(attributeName, other.attributeName)
				&& Objects.equals(attributeValue, other.attributeValue)
				&& Objects.equals(attributeValueOptions, other.attributeValueOptions);
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
