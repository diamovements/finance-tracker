package org.example.entity;

public enum RecurringFrequency {
    DAILY {
        @Override
        public String toString() {
            return "Каждый день";
        }
    },
    WEEKLY {
        @Override
        public String toString() {
            return "Каждая неделя";
        }
    },
    MONTHLY {
        @Override
        public String toString() {
            return "Каждый месяц";
        }
    }
}
