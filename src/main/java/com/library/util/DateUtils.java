package com.library.util;

import java.time.LocalDate;

public class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Cette classe ne peut pas être instanciée");
    }

    /**
     * Retourne la date du jour.
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Ajoute un nombre de jours spécifique à une date donnée.
     */
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * Vérifie si une date limite est dépassée par rapport à aujourd'hui.
     */
    public static boolean isOverdue(LocalDate dueDate) {
        if (dueDate == null) {
            return false;
        }
        return today().isAfter(dueDate);
    }
}