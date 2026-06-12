package com.taskflow.dto;

/**
 * Per-user task counts, e.g. for a dashboard.
 */
public record TaskStats(long todo, long inProgress, long done, long total) {
}
