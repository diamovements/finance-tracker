package org.example.dto.request;

import org.example.entity.RecurringFrequency;

public record DeleteLimitRequest(RecurringFrequency frequency) {
}
