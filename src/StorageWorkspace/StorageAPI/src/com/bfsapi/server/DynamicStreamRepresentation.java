/*
 * Copyright (c) Sohu Inc. 2012
 * 
 */
package com.bfsapi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

/**
 * @author Samuel
 *
 */
public class DynamicStreamRepresentation extends OutputRepresentation {

	// TODO: this is sample code. Change it.
    private InputStream data;

    public DynamicStreamRepresentation(InputStream data, MediaType mediaType) {
        super(mediaType);
        this.data = data;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
    	byte[] buffer = new byte[256];
    	int size;
    	System.out.printf("DynamicStreamRepresentation.write enter\n");
		while ((size = this.data.read(buffer)) != -1){
    		outputStream.write(buffer, 0, size);
    		System.out.printf("DynamicStreamRepresentation read %d\n", size);
    	}
		System.out.printf("DynamicStreamRepresentation.write exit\n");
    }

}