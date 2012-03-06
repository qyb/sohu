package com.scss.core.security;

/**
 * 
 * @author Samuel
 *
 */
public enum SignatureAlgorithmic {
	
	V1("HmacSHA1"),	V2("HmacSHA256");
	
	private String value;

    private SignatureAlgorithmic(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
