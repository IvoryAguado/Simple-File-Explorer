package com.sds.ivor.fileexplorer;

public class AbortionFlag {

	private boolean aborted = false;
	
	public synchronized void abort()
	{
		aborted = true;
	}
	public synchronized boolean isAborted()
	{
		return aborted;
	}
}
