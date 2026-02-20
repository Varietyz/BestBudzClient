package com.bestbudz.engine.gpu;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPUMonitor {

	private static volatile GPUMonitor instance;
	private static final Object instanceLock = new Object();

	private final AtomicBoolean monitoringEnabled = new AtomicBoolean(false);
	private final AtomicBoolean healthCheckEnabled = new AtomicBoolean(true);
	private ScheduledExecutorService monitorExecutor;

	private final AtomicLong totalGPUOperations = new AtomicLong(0);
	private final AtomicLong successfulGPUOperations = new AtomicLong(0);
	private final AtomicLong failedGPUOperations = new AtomicLong(0);
	private final AtomicLong contextAcquisitions = new AtomicLong(0);
	private final AtomicLong contextAcquisitionFailures = new AtomicLong(0);
	private final AtomicLong totalContextTime = new AtomicLong(0);
	private final AtomicLong maxContextTime = new AtomicLong(0);

	private final ConcurrentLinkedQueue<ErrorEvent> recentErrors = new ConcurrentLinkedQueue<>();
	private final AtomicLong totalErrors = new AtomicLong(0);
	private final AtomicReference<String> lastError = new AtomicReference<>("");
	private final AtomicLong lastErrorTime = new AtomicLong(0);

	private final AtomicReference<HealthStatus> currentHealth = new AtomicReference<>(HealthStatus.UNKNOWN);
	private final AtomicLong lastHealthCheck = new AtomicLong(0);
	private final Map<String, Object> healthMetrics = new ConcurrentHashMap<>();

	private static final int MAX_ERROR_HISTORY = 100;
	private static final long HEALTH_CHECK_INTERVAL_MS = 5000;
	private static final long ERROR_RETENTION_MS = 300000;
	private static final long CONTEXT_TIME_WARNING_MS = 1000;

	public enum HealthStatus {
		HEALTHY,
		WARNING,
		DEGRADED,
		CRITICAL,
		FAILED,
		UNKNOWN
	}

	public static class ErrorEvent {
		public final long timestamp;
		public final String operation;
		public final String error;
		public final String thread;
		public final StackTraceElement[] stackTrace;

		public ErrorEvent(String operation, String error, String thread) {
			this.timestamp = System.currentTimeMillis();
			this.operation = operation;
			this.error = error;
			this.thread = thread;
			this.stackTrace = Thread.currentThread().getStackTrace();
		}

		@Override
		public String toString() {
			return String.format("[%tT] %s on %s: %s", timestamp, operation, thread, error);
		}
	}

	private GPUMonitor() {

	}

	public static GPUMonitor getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				if (instance == null) {
					instance = new GPUMonitor();
				}
			}
		}
		return instance;
	}

	public void startMonitoring() {
		if (monitoringEnabled.getAndSet(true)) {
			return;
		}

		System.out.println("[GPU Monitor] Starting GPU system monitoring...");

		monitorExecutor = Executors.newScheduledThreadPool(1, r -> {
			Thread t = new Thread(r, "GPU-Monitor");
			t.setDaemon(true);
			return t;
		});

		monitorExecutor.scheduleAtFixedRate(
			this::performHealthCheck,
			HEALTH_CHECK_INTERVAL_MS,
			HEALTH_CHECK_INTERVAL_MS,
			TimeUnit.MILLISECONDS
		);

		monitorExecutor.scheduleAtFixedRate(
			this::performCleanup,
			ERROR_RETENTION_MS,
			ERROR_RETENTION_MS,
			TimeUnit.MILLISECONDS
		);

		System.out.println("[GPU Monitor] ✅ Monitoring started");
	}

	public void stopMonitoring() {
		if (!monitoringEnabled.getAndSet(false)) {
			return;
		}

		System.out.println("[GPU Monitor] Stopping GPU system monitoring...");

		if (monitorExecutor != null && !monitorExecutor.isShutdown()) {
			monitorExecutor.shutdown();
			try {
				if (!monitorExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
					monitorExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				monitorExecutor.shutdownNow();
			}
		}

		System.out.println("[GPU Monitor] ✅ Monitoring stopped");
	}

	public void recordGPUOperation(String operation, boolean success, long durationMs) {
		totalGPUOperations.incrementAndGet();

		if (success) {
			successfulGPUOperations.incrementAndGet();
		} else {
			failedGPUOperations.incrementAndGet();
			recordError(operation, "Operation failed", Thread.currentThread().getName());
		}

		if (durationMs > CONTEXT_TIME_WARNING_MS) {
			recordError(operation, "Long operation duration: " + durationMs + "ms", Thread.currentThread().getName());
		}
	}

	public void recordContextAcquisition(String operation, boolean success, long durationMs) {
		contextAcquisitions.incrementAndGet();
		totalContextTime.addAndGet(durationMs);

		long currentMax;
		do {
			currentMax = maxContextTime.get();
		} while (durationMs > currentMax && !maxContextTime.compareAndSet(currentMax, durationMs));

		if (!success) {
			contextAcquisitionFailures.incrementAndGet();
			recordError(operation, "Context acquisition failed after " + durationMs + "ms", Thread.currentThread().getName());
		} else if (durationMs > CONTEXT_TIME_WARNING_MS) {
			recordError(operation, "Slow context acquisition: " + durationMs + "ms", Thread.currentThread().getName());
		}
	}

	public void recordError(String operation, String error, String thread) {
		totalErrors.incrementAndGet();
		lastError.set(error);
		lastErrorTime.set(System.currentTimeMillis());

		ErrorEvent errorEvent = new ErrorEvent(operation, error, thread);
		recentErrors.offer(errorEvent);

		while (recentErrors.size() > MAX_ERROR_HISTORY) {
			recentErrors.poll();
		}

		if (monitoringEnabled.get()) {
			System.err.println("[GPU Monitor] ERROR: " + errorEvent);
		}

		updateHealthBasedOnErrors();
	}

	private void performHealthCheck() {
		if (!healthCheckEnabled.get()) {
			return;
		}

		try {
			HealthStatus newHealth = calculateHealthStatus();
			HealthStatus oldHealth = currentHealth.getAndSet(newHealth);
			lastHealthCheck.set(System.currentTimeMillis());

			if (oldHealth != newHealth) {
				System.out.println("[GPU Monitor] Health status changed: " + oldHealth + " -> " + newHealth);

				if (newHealth == HealthStatus.CRITICAL || newHealth == HealthStatus.FAILED) {
					System.err.println("[GPU Monitor] ⚠️ CRITICAL: GPU system health is " + newHealth);
					logDetailedDiagnostics();
				}
			}

			updateHealthMetrics();

		} catch (Exception e) {
			System.err.println("[GPU Monitor] Error during health check: " + e.getMessage());
			recordError("HealthCheck", e.getMessage(), Thread.currentThread().getName());
		}
	}

	private HealthStatus calculateHealthStatus() {

		if (!GPURenderingEngine.isEnabled()) {
			return HealthStatus.FAILED;
		}

		long totalOps = totalGPUOperations.get();
		long failedOps = failedGPUOperations.get();
		long contextFailures = contextAcquisitionFailures.get();
		long totalContextAcqs = contextAcquisitions.get();

		double operationFailureRate = totalOps > 0 ? (double) failedOps / totalOps : 0.0;
		double contextFailureRate = totalContextAcqs > 0 ? (double) contextFailures / totalContextAcqs : 0.0;

		long recentErrorCount = recentErrors.stream()
			.mapToLong(e -> System.currentTimeMillis() - e.timestamp < 60000 ? 1 : 0)
			.sum();

		if (operationFailureRate > 0.5 || contextFailureRate > 0.3 || recentErrorCount > 20) {
			return HealthStatus.CRITICAL;
		} else if (operationFailureRate > 0.2 || contextFailureRate > 0.1 || recentErrorCount > 10) {
			return HealthStatus.DEGRADED;
		} else if (operationFailureRate > 0.05 || contextFailureRate > 0.02 || recentErrorCount > 5) {
			return HealthStatus.WARNING;
		} else if (totalOps > 0) {
			return HealthStatus.HEALTHY;
		} else {
			return HealthStatus.UNKNOWN;
		}
	}

	private void updateHealthBasedOnErrors() {

		long now = System.currentTimeMillis();
		long recentErrors = this.recentErrors.stream()
			.mapToLong(e -> now - e.timestamp < 5000 ? 1 : 0)
			.sum();

		if (recentErrors > 10) {
			currentHealth.set(HealthStatus.CRITICAL);
			System.err.println("[GPU Monitor] ⚠️ Rapid error accumulation detected: " + recentErrors + " errors in 5 seconds");
		}
	}

	private void updateHealthMetrics() {
		long totalOps = totalGPUOperations.get();
		long successOps = successfulGPUOperations.get();
		long totalContextAcqs = contextAcquisitions.get();
		long totalTime = totalContextTime.get();

		healthMetrics.put("total_operations", totalOps);
		healthMetrics.put("success_rate", totalOps > 0 ? (double) successOps / totalOps : 0.0);
		healthMetrics.put("average_context_time", totalContextAcqs > 0 ? (double) totalTime / totalContextAcqs : 0.0);
		healthMetrics.put("max_context_time", maxContextTime.get());
		healthMetrics.put("recent_errors", recentErrors.size());
		healthMetrics.put("last_health_check", lastHealthCheck.get());

		if (GPURenderingEngine.isEnabled()) {
			healthMetrics.put("gpu_enabled", true);
			healthMetrics.put("gpu_width", GPURenderingEngine.getWidth());
			healthMetrics.put("gpu_height", GPURenderingEngine.getHeight());
		} else {
			healthMetrics.put("gpu_enabled", false);
		}
	}

	private void performCleanup() {
		long cutoffTime = System.currentTimeMillis() - ERROR_RETENTION_MS;

		recentErrors.removeIf(error -> error.timestamp < cutoffTime);

	}

	private void logDetailedDiagnostics() {
		System.err.println("=== GPU SYSTEM DIAGNOSTICS ===");
		System.err.println("Health Status: " + currentHealth.get());
		System.err.println("GPU Enabled: " + GPURenderingEngine.isEnabled());
		System.err.println("Total Operations: " + totalGPUOperations.get());
		System.err.println("Success Rate: " + String.format("%.2f%%", getSuccessRate() * 100));
		System.err.println("Context Acquisitions: " + contextAcquisitions.get());
		System.err.println("Context Failure Rate: " + String.format("%.2f%%", getContextFailureRate() * 100));
		System.err.println("Average Context Time: " + String.format("%.2fms", getAverageContextTime()));
		System.err.println("Max Context Time: " + maxContextTime.get() + "ms");
		System.err.println("Recent LoadingErrorScreen: " + recentErrors.size());
		System.err.println("Last Error: " + lastError.get());

		if (GPURenderingEngine.isEnabled()) {
			System.err.println("GPU Statistics: " + GPURenderingEngine.getGPUStatistics());
		}

		System.err.println("=== END DIAGNOSTICS ===");
	}

	public HealthStatus getHealthStatus() {
		return currentHealth.get();
	}

	public boolean isHealthy() {
		HealthStatus status = currentHealth.get();
		return status == HealthStatus.HEALTHY || status == HealthStatus.WARNING;
	}

	public double getSuccessRate() {
		long total = totalGPUOperations.get();
		return total > 0 ? (double) successfulGPUOperations.get() / total : 0.0;
	}

	public double getContextFailureRate() {
		long total = contextAcquisitions.get();
		return total > 0 ? (double) contextAcquisitionFailures.get() / total : 0.0;
	}

	public double getAverageContextTime() {
		long total = contextAcquisitions.get();
		return total > 0 ? (double) totalContextTime.get() / total : 0.0;
	}

	public String getLastError() {
		return lastError.get();
	}

	public long getLastErrorTime() {
		return lastErrorTime.get();
	}

	public int getRecentErrorCount() {
		return recentErrors.size();
	}

	public String getComprehensiveReport() {
		StringBuilder report = new StringBuilder();
		report.append("=== GPU MONITOR REPORT ===\n");
		report.append("Health Status: ").append(currentHealth.get()).append("\n");
		report.append("Monitoring Enabled: ").append(monitoringEnabled.get()).append("\n");
		report.append("GPU Enabled: ").append(GPURenderingEngine.isEnabled()).append("\n");
		report.append("\n--- PERFORMANCE METRICS ---\n");
		report.append("Total Operations: ").append(totalGPUOperations.get()).append("\n");
		report.append("Successful Operations: ").append(successfulGPUOperations.get()).append("\n");
		report.append("Failed Operations: ").append(failedGPUOperations.get()).append("\n");
		report.append("Success Rate: ").append(String.format("%.2f%%", getSuccessRate() * 100)).append("\n");
		report.append("Context Acquisitions: ").append(contextAcquisitions.get()).append("\n");
		report.append("Context Failures: ").append(contextAcquisitionFailures.get()).append("\n");
		report.append("Context Failure Rate: ").append(String.format("%.2f%%", getContextFailureRate() * 100)).append("\n");
		report.append("Average Context Time: ").append(String.format("%.2fms", getAverageContextTime())).append("\n");
		report.append("Max Context Time: ").append(maxContextTime.get()).append("ms\n");
		report.append("\n--- ERROR TRACKING ---\n");
		report.append("Total LoadingErrorScreen: ").append(totalErrors.get()).append("\n");
		report.append("Recent LoadingErrorScreen: ").append(recentErrors.size()).append("\n");
		report.append("Last Error: ").append(lastError.get()).append("\n");
		report.append("Last Error Time: ").append(lastErrorTime.get() > 0 ?
			String.format("%tT", lastErrorTime.get()) : "None").append("\n");

		if (GPURenderingEngine.isEnabled()) {
			report.append("\n--- GPU STATISTICS ---\n");
			report.append(GPURenderingEngine.getGPUStatistics()).append("\n");
		}

		report.append("\n--- RECENT ERRORS (Last 10) ---\n");
		recentErrors.stream()
			.skip(Math.max(0, recentErrors.size() - 10))
			.forEach(error -> report.append(error.toString()).append("\n"));

		report.append("=== END REPORT ===");
		return report.toString();
	}

	public void setHealthCheckEnabled(boolean enabled) {
		healthCheckEnabled.set(enabled);
	}

	public void triggerHealthCheck() {
		if (monitoringEnabled.get()) {
			performHealthCheck();
		}
	}

	public void resetMetrics() {
		totalGPUOperations.set(0);
		successfulGPUOperations.set(0);
		failedGPUOperations.set(0);
		contextAcquisitions.set(0);
		contextAcquisitionFailures.set(0);
		totalContextTime.set(0);
		maxContextTime.set(0);
		totalErrors.set(0);
		recentErrors.clear();
		lastError.set("");
		lastErrorTime.set(0);
		currentHealth.set(HealthStatus.UNKNOWN);
		healthMetrics.clear();

		System.out.println("[GPU Monitor] Metrics reset");
	}
}
