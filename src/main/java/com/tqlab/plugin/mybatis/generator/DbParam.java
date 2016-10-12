/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author John Lee
 *
 */
public class DbParam {

	private String objectName;
	private FullyQualifiedJavaType type;

	/**
	 * @return the objectName
	 */
	public final String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public final void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the type
	 */
	public final FullyQualifiedJavaType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(FullyQualifiedJavaType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return type.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbParam other = (DbParam) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.toString().equals(other.type.toString()))
			return false;
		return true;
	}

}
