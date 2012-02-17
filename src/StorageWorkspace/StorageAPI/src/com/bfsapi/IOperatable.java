/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi;

/**
 * To represents an object is able to be operated.
 * 
 * @author Samuel
 *
 */
public interface IOperatable {
	OperationResult Operate(Operation op);
}
