package com.souche.android.framework.net.upload;

import java.io.IOException;

public class CancelException extends IOException {

    private static final long serialVersionUID = 1L;

    public CancelException(String string) {
		super(string);
	}

} 