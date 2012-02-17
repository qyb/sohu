/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi;

/**
 * @author Samuel
 *
 */
public class Operation {
	//public String BucketName;
	//public String ObjectKey;
	public IOperatable Target;
	public IAccessor Performer;
	public EnumOperator Operator;
	public OperationResult Result;
	
	public OperationResult perform() {
		return Target.Operate(this);
	}
}
