package com.adham.taskmanagement.common.web;

import com.adham.taskmanagement.common.exception.InvalidRequestException;
import com.adham.taskmanagement.common.exception.PreconditionRequiredException;

public final class EntityTagVersion {

    private EntityTagVersion() {
    }

    public static long parseRequired(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new PreconditionRequiredException(
                    "If-Match header is required"
            );
        }

        String value = ifMatch.trim();

        if (value.length() < 3
                || value.charAt(0) != '"'
                || value.charAt(value.length() - 1) != '"') {
            throw invalidIfMatch();
        }

        String versionText = value.substring(1, value.length() - 1);

        try {
            long version = Long.parseLong(versionText);

            if (version < 0) {
                throw invalidIfMatch();
            }

            return version;
        } catch (NumberFormatException exception) {
            throw invalidIfMatch();
        }
    }

    public static String format(long version) {
        return "\"" + version + "\"";
    }

    private static InvalidRequestException invalidIfMatch() {
        return new InvalidRequestException(
                "If-Match must contain one quoted, non-negative task version"
        );
    }
}
