package info.rubico.mock4aj.codegen.cglib.proxies;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.exceptions.NotAnInstanceToProxy;
import info.rubico.mock4aj.api.exceptions.UnproxiableType;
import info.rubico.mock4aj.api.proxies.WeavedProxyFactory;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.weavers.noweaving.NoWeavingWeaver;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class CglibWeavedProxyFactoryTest { // NOPMD

    private WeavedProxyFactory factory;
    private Weaver noWeavingAdapter;

    @Before
    public void setupFactoryWithNoWeaving() {
        factory = new CglibWeavedProxyFactory();
        noWeavingAdapter = new NoWeavingWeaver();
    }

    @Test
    public void canProxyClassInstance() {
        Date anObject = new Date();

        Date proxy = factory.createWeavedProxy(anObject, noWeavingAdapter);
        final long aTime = 5L;
        proxy.setTime(aTime);

        assertEquals(aTime, proxy.getTime());
    }

    @Test
    public void canProxyMockedClassInstance() {
        Date dateMocked = mock(Date.class);

        Date proxy = factory.createWeavedProxy(dateMocked, noWeavingAdapter);
        proxy.getTime();

        verify(dateMocked).getTime();
    }

    @Test
    public void canProxyClassWithNoDefaultConstructor() {
        ClassWithNoDefaultConstructor anObject = new ClassWithNoDefaultConstructor(); // NOPMD
        factory.createWeavedProxy(anObject, noWeavingAdapter);
    }

    @Test
    public void canProxyMockedInterfaceWithGenericsInstance() {
        @SuppressWarnings("unchecked")
        List<String> dateMocked = mock(List.class);

        List<String> proxy = factory.createWeavedProxy(dateMocked, noWeavingAdapter);
        final String stringAdded = "test";
        proxy.add(stringAdded);

        verify(dateMocked).add(stringAdded);
    }

    @Test(expected = UnproxiableType.class)
    public void canNotProxyFinalClassInstance() {
        AFinalClass anObjectOfFinalClass = new AFinalClass();
        factory.createWeavedProxy(anObjectOfFinalClass, noWeavingAdapter);
    }

    public void canProxyAnonymousClassInstance() {
        Object anynymousObject = new Object() {
        };
        factory.createWeavedProxy(anynymousObject, noWeavingAdapter);
    }

    @Test(expected = UnproxiableType.class)
    public void canNotProxyPrimitiveTypeInstance() {
        final int primitiveInt = 6;
        factory.createWeavedProxy(primitiveInt, noWeavingAdapter);
    }

    @Test(expected = NotAnInstanceToProxy.class)
    public void canNotProxyDirectlyClassWithoutAnInstance() {
        factory.createWeavedProxy(Date.class, noWeavingAdapter);
    }

    @Test
    public void givenAClassWhenProxyThenMethodsAreForwarded() {
        SimpleClass anObject = new SimpleClass();

        SimpleClass proxy = factory.createWeavedProxy(anObject, noWeavingAdapter);

        Object paramObject = new Object();
        assertEquals(paramObject, proxy.returnItsParam(paramObject));
    }

    @Test
    public void givenAMockWhenProxyThenMethodsAreForwarded() {
        SimpleClass aMock = mock(SimpleClass.class);
        Object paramObject = new Object();
        given(aMock.returnItsParam(paramObject)).willReturn(paramObject);

        SimpleClass proxy = factory.createWeavedProxy(aMock, noWeavingAdapter);

        assertEquals(paramObject, proxy.returnItsParam(paramObject));
        verify(aMock).returnItsParam(paramObject);
    }

    @Test
    public void whenProxyThenTheProxyIsWeavedUsingTheWeaverAdapter() {
        Date anObject = new Date();
        Weaver weaverMocked = mock(NoWeavingWeaver.class);
        given(weaverMocked.weaveClassBytes(anyString(), isA(byte[].class))).willCallRealMethod();

        factory.createWeavedProxy(anObject, weaverMocked);
        verify(weaverMocked).weaveClassBytes(contains(expectedProxyName(anObject)),
                                             isA(byte[].class));
    }

    @Test
    public void givenProxyWhenProxyThenTheSuperclassIsTheRealClassProxiedFirst() {
        Date dateMocked = mock(Date.class);
        Date proxy = factory.createWeavedProxy(dateMocked, noWeavingAdapter);

        Date proxyOfProxy = factory.createWeavedProxy(proxy, noWeavingAdapter);

        assertEquals("should have the enhanced mock class has superclass.",
                     Date.class, superclassOf(proxyOfProxy));
    }

    @Test
    public void whenProxyThenTheNameHasTheBaseClassNameAndTheTag() {
        Date anObject = new Date();
        Date proxy = factory.createWeavedProxy(anObject, noWeavingAdapter);
        assertThat(proxy.getClass().getName(),
                   containsString(expectedProxyName(anObject)));
    }

    @Test
    public void noProxyCachingBecauseItCausesProblemsWithWeaving() {
        Date aDate = new Date();

        Date aDateProxy = factory.createWeavedProxy(aDate, noWeavingAdapter);
        Date sameDateProxy = factory.createWeavedProxy(aDate, noWeavingAdapter);

        assertNotSame(sameDateProxy.getClass(), aDateProxy.getClass());
    }

    private String expectedProxyName(final Object anObject) {
        String proxyName = String.format("%s$$Enhancer%s$",
                                         anObject.getClass().getName(),
                                         "ProxyByMock4Aj");
        return proxyName;
    }

    private static Class<?> superclassOf(final Object object) {
        return object.getClass().getSuperclass();
    }

    private static class SimpleClass {
        public Object returnItsParam(final Object arg1) {
            return arg1;
        }
    }

    private final class AFinalClass { // NOPMD

    }

    private class ClassWithNoDefaultConstructor {
        private final int x; // NOPMD

        private ClassWithNoDefaultConstructor() {
            this.x = 0;
        }

        public ClassWithNoDefaultConstructor(int x) { // NOPMD
            this.x = x;
        }
    }

}
