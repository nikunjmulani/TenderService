package com.tender.utils;

import java.util.Arrays;

import org.apache.catalina.valves.StuckThreadDetectionValve;

public class TomcatStuckThreads {
	public static void main(String arg[]) {
		killStuckThreads();
	}

	public static void killStuckThreads() {
		StuckThreadDetectionValve obj = new StuckThreadDetectionValve();
		long[] stuckThreads = obj.getStuckThreadIds();
		System.out.println("Stucked Threads:" + stuckThreads);
		
		Thread currentThread = Thread.currentThread();
		ThreadGroup threadGroup = getRootThreadGroup(currentThread);
		int allActiveThreads = threadGroup.activeCount();
		Thread[] allThreads = new Thread[allActiveThreads];
		threadGroup.enumerate(allThreads);

		for (int i = 0; i < allThreads.length; i++) {
			Thread thread = allThreads[i];
			long id = thread.getId();
			
			if (Arrays.binarySearch(stuckThreads, id) >= 0) {
				thread.interrupt();
				System.out.println("Killed Thread:"+id);
			}
			
			System.out.println("End");
		}
	}

	private static ThreadGroup getRootThreadGroup(Thread thread) {
		ThreadGroup rootGroup = thread.getThreadGroup();
		System.out.println("Thread Group: "+rootGroup.getName());
		while (true) {
			ThreadGroup parentGroup = rootGroup.getParent();
			if (parentGroup == null) {
				break;
			}
			rootGroup = parentGroup;
		}
		return rootGroup;
	}

}
