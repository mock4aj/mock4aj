package info.rubico.mock4aj.weavers.noweaving;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NoWeavingWeaverTest { // NOPMD

    private NoWeavingWeaver noWeavingAdapter;

    @Before
    public void configureNoWeavingAdaptor() {
        noWeavingAdapter = new NoWeavingWeaver();
    }

    @Test
    public void shouldAlwaysReturnSameBytecode() {
        byte[] bytecode = "some bytes".getBytes();
        byte[] newBytecode = noWeavingAdapter.weaveClassBytes("classname", bytecode);
        assertArrayEquals("Bytecode should be untouched.",
                          bytecode, newBytecode);
    }

}
