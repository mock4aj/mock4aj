package info.rubico.mock4aj.codegen.cglib.classutils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.internal.classutils.SuperclassFinder;

import java.util.Date;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.junit.Before;
import org.junit.Test;

public class CglibNearestRealSuperclassFinderTest { // NOPMD

    private SuperclassFinder superclassFinder;

    @Before
    public void setupAFinder() {
        superclassFinder = new CglibNearestRealSuperclassFinder();
    }

    @Test
    public void givenAClassThenTheSuperclassIsTheClassItSelf() {
        Date aDate = new Date();
        Object superclass = superclassFinder.findSuperclass(aDate);
        assertEquals("should have the proxied class has superclass.",
                     Date.class, superclass);
    }

    @Test
    public void givenAMockThenTheSuperclassIsTheClassMocked() {
        Date dateMocked = mock(Date.class);
        Object superclass = superclassFinder.findSuperclass(dateMocked);
        assertEquals("should have the enhanced mock class has superclass.",
                     Date.class, superclass);
    }

    @Test
    public void givenCglibEnhancedProxyThenTheSuperclassIsTheRealClass() {
        final boolean withFactoryMarker = true;
        Date enhancedDate = makeEnhancedObject(Date.class, withFactoryMarker);
        Object superclass = superclassFinder.findSuperclass(enhancedDate);
        assertEquals("should have the real class has superclass.",
                     Date.class, superclass);
    }

    @Test
    public void givenCglibEnhancedProxyWithoutFactoryMarkerThenTheSuperclassIsTheRealClass() {
        final boolean withoutFactoryMarker = false;
        Date enhancedDate = makeEnhancedObject(Date.class, withoutFactoryMarker);
        Object superclass = superclassFinder.findSuperclass(enhancedDate);
        assertEquals("should have the real class has superclass.",
                     Date.class, superclass);
    }

    @Test
    public void givenDirectlyGeneratedClassThenTheSuperclassIsTheRealClass() {
        BeanGenerator someGenerator = new BeanGenerator();
        someGenerator.addProperty("SOME_FIELD", Integer.class);
        someGenerator.setSuperclass(Date.class);
        Object generatedObject = someGenerator.create();

        Object superclass = superclassFinder.findSuperclass(generatedObject);

        assertEquals("should have the real class has superclass.",
                     Date.class, superclass);
    }

    @SuppressWarnings("unchecked")
    private <T> T makeEnhancedObject(Class<T> clazz, boolean withFactory) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setUseCache(false);
        enhancer.setUseFactory(withFactory);
        enhancer.setCallback(NoOp.INSTANCE);
        return (T) enhancer.create();
    }

}
