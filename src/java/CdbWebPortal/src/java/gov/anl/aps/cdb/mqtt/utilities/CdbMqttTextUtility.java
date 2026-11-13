/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.utilities;

/** Text bounding and line-diff helpers shared by MQTT events that carry free text. */
public final class CdbMqttTextUtility {

    public static final int MAX_TEXT_LENGTH = 16 * 1024;

    private CdbMqttTextUtility() {
    }

    /** Line diff of the changed middle section: removed lines as '-', added lines as '+'. */
    public static String createLineDiff(String oldText, String newText) {
        String[] oldLines = lines(oldText);
        String[] newLines = lines(newText);
        int prefix = 0;
        while (prefix < oldLines.length && prefix < newLines.length
                && oldLines[prefix].equals(newLines[prefix])) {
            prefix++;
        }
        int suffix = 0;
        while (suffix < oldLines.length - prefix && suffix < newLines.length - prefix
                && oldLines[oldLines.length - suffix - 1]
                        .equals(newLines[newLines.length - suffix - 1])) {
            suffix++;
        }

        StringBuilder result = new StringBuilder();
        for (int i = prefix; i < oldLines.length - suffix; i++) {
            result.append('-').append(oldLines[i]).append('\n');
        }
        for (int i = prefix; i < newLines.length - suffix; i++) {
            result.append('+').append(newLines[i]).append('\n');
        }
        return result.toString();
    }

    public static int length(String value) {
        return value == null ? 0 : value.length();
    }

    public static boolean exceedsLimit(String value) {
        return length(value) > MAX_TEXT_LENGTH;
    }

    public static String truncate(String value) {
        return exceedsLimit(value) ? value.substring(0, MAX_TEXT_LENGTH) : value;
    }

    private static String[] lines(String value) {
        return value == null || value.isEmpty() ? new String[0] : value.split("\\R", -1);
    }
}
