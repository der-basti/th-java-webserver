package de.th.wildau.dsc.sne.webserver.test;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;

public class SystemTest {

	public static void main(String[] args) {

		System.out.println("Start time: "
				+ new Date(ManagementFactory.getRuntimeMXBean().getStartTime())
						.toString());

		String nameOS = "os.name";
		String versionOS = "os.version";
		String architectureOS = "os.arch";
		System.out.println("The information about OS");
		System.out.println("========================");
		System.out.println("Name of the OS: " + System.getProperty(nameOS));
		System.out.println("Version of the OS: "
				+ System.getProperty(versionOS));
		System.out.println("Architecture of the OS: "
				+ System.getProperty(architectureOS));

		System.out.println();
		OperatingSystemMXBean osMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		System.out.println("SystemLoadAverage: "
				+ osMXBean.getSystemLoadAverage());
		System.out.println("Uptime: "
				+ new Date(runtimeMXBean.getUptime()).toString());

		System.out.println();
		
		long start = System.currentTimeMillis();
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long[] allThreadIds = threadMXBean.getAllThreadIds();
		System.out.println("Total JVM Thread count: " + allThreadIds.length);
		long nano = 0;
		for (long id : allThreadIds) {
			nano += threadMXBean.getThreadCpuTime(id);
		}
		System.out.printf("Total cpu time: %s ms; real time: %s", nano / 1E6,
				(System.currentTimeMillis() - start));
		System.out.println();

		System.out.println();
		System.out.println("Available processors (cores): "
				+ Runtime.getRuntime().availableProcessors());

		System.out.println("Free memory (bytes): "
				+ Runtime.getRuntime().freeMemory());

		long maxMemory = Runtime.getRuntime().maxMemory();
		System.out.println("Maximum memory (bytes): "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

		System.out.println("Total memory (bytes): "
				+ Runtime.getRuntime().totalMemory());

		for (File root : File.listRoots()) {
			System.out.println("File system root: " + root.getAbsolutePath());
			System.out.println("Total space (bytes): " + root.getTotalSpace());
			System.out.println("Free space (bytes): " + root.getFreeSpace());
			System.out
					.println("Usable space (bytes): " + root.getUsableSpace());
		}

	}
}
