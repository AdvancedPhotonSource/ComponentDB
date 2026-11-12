/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.utilities;

import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMqttTextUtilityTest {

    @Test
    public void lineDiffKeepsOnlyChangedMiddleLines() {
        assertEquals("-b\n+B\n+c2\n",
                CdbMqttTextUtility.createLineDiff("a\nb\nd", "a\nB\nc2\nd"));
    }

    @Test
    public void lineDiffHandlesNullAndCrLf() {
        assertEquals("+x\n", CdbMqttTextUtility.createLineDiff(null, "x"));
        assertEquals("-x\n", CdbMqttTextUtility.createLineDiff("x", ""));
        assertEquals("", CdbMqttTextUtility.createLineDiff("a\r\nb", "a\nb"));
    }

    @Test
    public void truncatesOnlyAboveLimit() {
        String atLimit = String.join("", Collections.nCopies(CdbMqttTextUtility.MAX_TEXT_LENGTH, "x"));
        String overLimit = atLimit + "y";

        assertSame(atLimit, CdbMqttTextUtility.truncate(atLimit));
        assertFalse(CdbMqttTextUtility.exceedsLimit(atLimit));
        assertTrue(CdbMqttTextUtility.exceedsLimit(overLimit));
        assertEquals(atLimit, CdbMqttTextUtility.truncate(overLimit));
        assertNull(CdbMqttTextUtility.truncate(null));
        assertEquals(0, CdbMqttTextUtility.length(null));
    }
}
