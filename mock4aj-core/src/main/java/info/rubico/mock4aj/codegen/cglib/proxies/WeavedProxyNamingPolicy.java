package info.rubico.mock4aj.codegen.cglib.proxies;

import net.sf.cglib.core.DefaultNamingPolicy;

/**
 * Default naming policy for a Weaved Proxy.
 */
public class WeavedProxyNamingPolicy extends DefaultNamingPolicy {

    public static final String CLASSNAME_SUFFIX = "ProxyByMock4Aj";

    public static final WeavedProxyNamingPolicy INSTANCE = new WeavedProxyNamingPolicy();

    @Override
    protected String getTag() {
        return CLASSNAME_SUFFIX;
    }

}