package org.example.dto;


public enum TransactionType {
    INCOME {
        @Override
        public String toString() {
            return "Доход";
        }
    },
    EXPENSE {
        @Override
        public String toString() {
            return "Расход";
        }
    }
}

