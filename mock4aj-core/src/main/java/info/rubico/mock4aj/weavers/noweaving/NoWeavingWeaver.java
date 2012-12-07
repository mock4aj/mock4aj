package info.rubico.mock4aj.weavers.noweaving;

import info.rubico.mock4aj.api.weaving.Weaver;

/**
 * A Fake Weaver Adapter that doesn't weave. It just return the same bytecode unchanged.
 * 
 * It could be use to disable momentarily the weaving but continue to generate mocks and proxies.
 */
public class NoWeavingWeaver implements Weaver {

    public void registerAspect(final Class<?> aspect) {
        // We don't really care
    }

    public void unregisterAspect(final Class<?> aspect) {
        // No aspect will be ever registered
    }

    public void reset() {
        // Nothing to reset since we don't weave anything
    }

    public byte[] weaveClassBytes(final String className, final byte[] bytes) {
        return bytes;
    }

}
