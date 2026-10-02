/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.jca;

import gov.anl.aps.mqtt.exception.MqttNotConfiguredException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/** Looks up a Payara MQTT JCA factory without binding consumers to its API version. */
public class JndiMqttConnectionProvider implements MqttConnectionProvider {
    private final String jndiName;

    public JndiMqttConnectionProvider(String jndiName) {
        if (jndiName == null || jndiName.trim().isEmpty()) throw new IllegalArgumentException("jndiName is required");
        this.jndiName = jndiName;
    }

    @Override public MqttConnection getConnection() throws MqttNotConfiguredException {
        try {
            Object factory = new InitialContext().lookup(jndiName);
            Object connection = factory.getClass().getMethod("getConnection").invoke(factory);
            if (connection == null) {
                throw new MqttNotConfiguredException(
                        "MQTT resource could not connect to its broker: " + jndiName, null);
            }
            return new ReflectiveConnection(connection);
        } catch (NamingException | ReflectiveOperationException | LinkageError ex) {
            throw new MqttNotConfiguredException("MQTT resource is unavailable: " + jndiName, unwrap(ex));
        }
    }

    private static Throwable unwrap(Throwable error) {
        return error instanceof InvocationTargetException && error.getCause() != null ? error.getCause() : error;
    }

    private static final class ReflectiveConnection implements MqttConnection {
        private final Object delegate;
        private final Method publish;
        private final Method close;
        ReflectiveConnection(Object delegate) throws NoSuchMethodException {
            this.delegate = delegate;
            publish = delegate.getClass().getMethod("publish", String.class, byte[].class, int.class, boolean.class);
            close = delegate.getClass().getMethod("close");
        }
        @Override public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {
            try { publish.invoke(delegate, topic, payload, qos, retained); }
            catch (InvocationTargetException ex) { throw asException(unwrap(ex)); }
        }
        @Override public void close() throws Exception {
            try { close.invoke(delegate); }
            catch (InvocationTargetException ex) { throw asException(unwrap(ex)); }
        }
        private static Exception asException(Throwable error) {
            return error instanceof Exception ? (Exception) error : new Exception(error);
        }
    }
}
