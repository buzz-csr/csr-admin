package com.naturalmotion.listener;

public interface CsrTask extends Runnable {

	public void stop();

	public boolean isRunning();
}
