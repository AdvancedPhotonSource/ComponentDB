/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.Log;

/** Pending pre-update state for an item log-entry update or delete. */
public final class ItemLogChange {

    public enum Type { UPDATE, DELETE }

    private final Type type;
    private final Log oldLog;
    private final Log newLog;

    private ItemLogChange(Type type, Log oldLog, Log newLog) {
        this.type = type;
        this.oldLog = oldLog;
        this.newLog = newLog;
    }

    public static ItemLogChange updated(Log oldLog, Log newLog) {
        return new ItemLogChange(Type.UPDATE, oldLog, newLog);
    }

    public static ItemLogChange deleted(Log oldLog) {
        return new ItemLogChange(Type.DELETE, oldLog, null);
    }

    public Type getType() { return type; }
    public Log getOldLog() { return oldLog; }
    public Log getNewLog() { return newLog; }
}
