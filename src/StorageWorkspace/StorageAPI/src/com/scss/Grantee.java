/**
 * Copyright Sohu Inc. 2012
 */
package com.scss;

/**
 * Grantee represents a rule of that a permission 
 * to a resource is granted to an accessor.
 * 
 * @author Samuel
 *
 */
public abstract class Grantee extends SubResource {
	protected Resource resource;
	protected IAccessor accessor;
	protected Permition permit;
}
