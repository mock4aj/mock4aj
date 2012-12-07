package info.rubico.mock4aj.api.proxies;

import info.rubico.mock4aj.api.weaving.Weaver;

/**
 * <p>
 * Helper to create various kind of weaved proxies. A proxy is a dynamically generated class where
 * calls to the proxy are forwarded to the object (instance) proxied. The proxy has the same methods
 * signatures than the proxied class.
 * </p>
 * <p>
 * A proxy is not a mock. Normally, we create a proxy of a mock to add weaving to it. Since it is
 * not possible to weave directly an existing class, the proxy is <strong>weaved during his
 * creation</strong> and <strong>forwards calls to the mock</strong>.
 * </p>
 * 
 * <h2>Creation process</h2>
 * <p>
 * Once a class is loaded by the Java Classloader, it is not possible to alter it so the weaving
 * process must occurs during the creation (bytecode generation) and before the class loading.
 * </p>
 * <p>
 * Proxies are weaved using a {@link Weaver} which is responsible to do the weaving during the
 * bytecode generation process and <strong>before the loading of the double's class occurs by the
 * classloader</strong>. So the resulting proxy class is directly weaved <strong>but the proxied
 * object (typically the mock) is left untouched.</strong>.
 * </p>
 * 
 * Basically, the process is (may be adapted depending on the implementation):
 * <ol>
 * <li>The proxy's bytecode is generated
 * <li>The generated bytecode is altered by the weaver (using the {@link Weaver})
 * <li>The generated and weaved class is loaded by Java
 * <li>An instance of the resulting class is returned.
 * </ol>
 * 
 */
public interface WeavedProxyFactory {

    <T> T createWeavedProxy(final T objectToProxy, final Weaver weaverAdapter);

}