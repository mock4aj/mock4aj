package info.rubico.mock4aj.api.calls;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class FictitiousSourceTest {

    private static final String FICTITIOUS_CLASS_SIMPLE_NAME = "AFictiveName";
    private static final String FICTITIOUS_PACKAGE_NAME = "info.rubico.test";
    private static final String FICTITIOUS_CLASS_NAME = String.format("%s.%s",
                                                                      FICTITIOUS_PACKAGE_NAME,
                                                                      FICTITIOUS_CLASS_SIMPLE_NAME);

    private FictitiousSource fictitiousSource;

    @Before
    public void createAFictiveSource() {
        fictitiousSource = new FictitiousSource(FICTITIOUS_CLASS_NAME);
    }

    @Test
    public void givenNameWithPackageShouldHaveSameName() {
        assertEquals(FICTITIOUS_CLASS_NAME, fictitiousSource.getName());
    }

    @Test
    public void givenNameInDefaultPackageShouldHaveSameName() {
        FictitiousSource fictiveSourceInDefaultPackage = new FictitiousSource(FICTITIOUS_CLASS_SIMPLE_NAME);
        assertEquals(FICTITIOUS_CLASS_SIMPLE_NAME, fictiveSourceInDefaultPackage.getName());
    }

    @Test
    public void byDefaultShouldBeAMethodCallerType() {
        assertSame(MethodCaller.class, fictitiousSource.getType());
    }

    @Test
    public void givenExtendingClassShouldHaveTheSameType() {
        fictitiousSource.extending(Date.class);
        assertSame(Date.class, fictitiousSource.getType());
    }

    @Test
    public void givenImplementingInterfaceShouldHaveTheSameType() {
        fictitiousSource.implementing(Cloneable.class);
        assertSame(Cloneable.class, fictitiousSource.getType());
    }

    @Test
    public void givenExtendingClassShouldKeepTheFictiveName() {
        fictitiousSource.extending(Date.class);
        assertEquals(FICTITIOUS_CLASS_NAME, fictitiousSource.getName());
    }

    @Test
    public void givenImplementingIterfaceShouldKeepTheFictiveName() {
        fictitiousSource.implementing(Cloneable.class);
        assertEquals(FICTITIOUS_CLASS_NAME, fictitiousSource.getName());
    }

    @Test
    public void extendingShouldReturnSameObjectToAllowChaining() {
        FictitiousSource ret = fictitiousSource.extending(Date.class);
        assertSame(fictitiousSource, ret);
    }

    @Test
    public void implmentingShouldReturnSameObjectToAllowChaining() {
        FictitiousSource ret = fictitiousSource.implementing(Cloneable.class);
        assertSame(fictitiousSource, ret);
    }
}
