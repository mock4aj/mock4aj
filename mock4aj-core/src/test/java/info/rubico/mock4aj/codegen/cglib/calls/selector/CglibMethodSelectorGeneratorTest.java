package info.rubico.mock4aj.codegen.cglib.calls.selector;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.UncallableType;
import info.rubico.mock4aj.codegen.cglib.classutils.CglibNearestRealSuperclassFinder;
import info.rubico.mock4aj.internal.MethodSelector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;

import org.junit.Before;
import org.junit.Test;

public class CglibMethodSelectorGeneratorTest { // NOPMD

    private MethodInterceptor callbackMock;
    private CglibMethodSelectorGenerator generator;

    @Before
    public void setupAWeavedCallSimulator() {
        callbackMock = mock(MethodInterceptor.class);
        generator = new CglibMethodSelectorGenerator();
    }

    @Test
    public void canSelectInterfaceTypes() {
        AnInterface interfaceImpl = new AnInterfaceImpl();

        AnInterface selector = generator.generateSelector(interfaceImpl, callbackMock);
        selector.aMethod();

        assertThat(selector, instanceOf(AnInterfaceImpl.class));
    }

    @Test
    public void canSelectRealObjects() {
        Date realDate = new Date();

        Date selector = generator.generateSelector(realDate, callbackMock);
        selector.getTime();
    }

    @Test
    public void canSelectMocksOfObjects() {
        Date dateMock = mock(Date.class);

        Date selector = generator.generateSelector(dateMock, callbackMock);
        selector.getTime();
    }

    @Test
    public void canSelectMocksOfInterfaces() {
        AnInterface mock = mock(AnInterface.class);

        AnInterface selector = generator.generateSelector(mock, callbackMock);
        selector.aMethod();
    }

    @Test
    public void canSelectAnonymous() {
        Object anonymous = new Object() {
        };
        Object selector = generator.generateSelector(anonymous, callbackMock);
        assertNotSame(selector.getClass(), Object.class);
        assertThat(selector, instanceOf(anonymous.getClass()));
    }

    @Test
    public void canSelectTypesWithGenerics() {
        List<String> aList = new ArrayList<String>();
        List<String> selector = generator.generateSelector(aList, callbackMock);
        selector.add("A_STRING");
        selector.get(0);
    }

    @Test(expected = UncallableType.class)
    public void canNotSelectFinalTargetClasses() {
        final long aLong = 5L;
        generator.generateSelector(aLong, callbackMock);
    }

    @Test
    public void whenCreateThenSelectorIsAStub() {
        Date mock = mock(Date.class);
        Date target = generator.generateSelector(mock, callbackMock);
        target.getTime();
        verifyZeroInteractions(mock);
    }

    @Test
    public void whenCreateThenSelectorIsNamedWithATagIndicatingThatItIsATargetSelector() {
        Date realDate = new Date();
        Date selector = generator.generateSelector(realDate, callbackMock);

        assertThat(selector.getClass().getName(),
                   containsString("MethodSelector"));
    }

    @Test(expected = Mock4AjException.class)
    public void givenUnknownErrorWhenCreateThenShouldBeBoxedIntoAMock4AjException() {
        Date anObject = new Date();
        CglibMethodSelectorGenerator factoryWithError = new TargetSelectorFactoryWithError();
        factoryWithError.generateSelector(anObject, callbackMock);
    }

    @Test
    public void shouldUseASuperClassFinderToDetermineTheSuperClass() {
        final Class<?> forcedSupertype = Date.class;
        CglibNearestRealSuperclassFinder finder = new CglibNearestRealSuperclassFinder() {
            @Override
            public Class<?> findSuperclass(Object objectToProxy) {
                return forcedSupertype;
            }
        };

        generator.setSuperclassFinder(finder);
        Object selector = generator.generateSelector(new Object(), callbackMock);

        assertThat(selector, instanceOf(forcedSupertype));
    }

    @Test
    public void whenCreateThenTheSelectorImplementsMethodSelector() {
        Date mock = mock(Date.class);
        Date selector = generator.generateSelector(mock, callbackMock);
        assertThat(selector, instanceOf(MethodSelector.class));
    }

    interface AnInterface {
        void aMethod();
    }

    private class AnInterfaceImpl implements AnInterface {
        public void aMethod() { // NOPMD
        }
    }

    private class TargetSelectorFactoryWithError extends CglibMethodSelectorGenerator {

        @Override
        protected <T> T instantiate(Class<?> proxyClass, MethodInterceptor callback) {
            throw new ClassCastException();
        }
    }

}
