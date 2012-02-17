/*
 * Copyright (c) Sohu Inc. 2012
 * 
 */
package com.bfsapi.server;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

/**
 * @author Samuel
 *
 */
public class DynamicFileRepresentation extends OutputRepresentation {

    private byte[] fileData;

    public DynamicFileRepresentation(byte[] fileData, long expectedSize, MediaType mediaType) {
        super(mediaType, expectedSize);
        this.fileData = fileData;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(fileData);
    }

}