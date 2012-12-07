package info.rubico.mock4aj.api.calls;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class ExistingSourceTest {

    @Test
    public void shouldHaveSameType() {
        ExistingSource existingSource = new ExistingSource(Date.class);
        assertSame(Date.class, existingSource.getType());
    }

    @Test
    public void shouldHaveSameName() {
        ExistingSource existingSource = new ExistingSource(java.util.Date.class);
        assertEquals("java.util.Date", existingSource.getName());
    }

    @Test
    public void givenInnerClassShouldHaveFullName() {
        ExistingSource existingSource = new ExistingSource(SomeInnerClass.class);
        assertEquals(String.format("%s$SomeInnerClass", this.getClass().getName()),
                     existingSource.getName());
    }

    class SomeInnerClass { // NOPMD

    }

}
