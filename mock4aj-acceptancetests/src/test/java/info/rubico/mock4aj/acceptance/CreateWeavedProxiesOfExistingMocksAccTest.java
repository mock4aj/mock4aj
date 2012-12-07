package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.createWeavedProxy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import info.rubico.mock4aj.acceptance.testdata.AbstractAspect;
import info.rubico.mock4aj.acceptance.testdata.ConcreteTarget;
import info.rubico.mock4aj.acceptance.testdata.MarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.OtherClass;
import info.rubico.mock4aj.acceptance.testdata.SecondMarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.Target;
import info.rubico.mock4aj.api.exceptions.NotAConcreteAspect;
import info.rubico.mock4aj.api.exceptions.NotAnAspect;

import java.util.Date;

import org.junit.Test;

/**
 * <h1>Create a weaved proxy of already created mock</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a unit tester, I want to be able to weave already created mocks because I could have created
 * the mock before with an unsupported framework or because it's a special kind of mock.
 * 
 * If the mock is already created it is not possible to weave it since it has already been loaded
 * into memory so I need to be able to create a weaved proxy that will forward calls to the original
 * mock.
 * </p>
 * 
 * 
 * <h2>Notes about the test</h2>
 * <p>
 * We are using Mockito to create standard unweaved mocks. The test could have been done with any
 * virtual mocking framework since we are just creating a proxy. The test <strong>doesn't use any
 * special integration</strong> for Mockito.
 * </p>
 */
public class CreateWeavedProxiesOfExistingMocksAccTest {

    /**
     * <pre>
     * | test |         |   proxy   |          | aspect |       |  mock   |
     * ========         =============          ==========       ===========
     *    * ----call---->.theMethod 
     *                       * ------forward--------------------->.theMethod
     *    ...
     * </pre>
     */
    @Test
    public void proxyShouldForwardToTheRealMockMethods() {
        Target mock = mock(Target.class);
        Target proxy = createWeavedProxy(mock, MarkTargetAtAspect.class);

        proxy.theMethod();

        verify(mock).theMethod(); // Call forwarded by the proxy to the mock
    }

    /**
     * <pre>
     * | test |         |   proxy   |          | aspect |       |  mock   |
     * ========         =============          ==========       ===========
     *    * ----call---->.theMethod 
     *    ...
     *                       * ------trigger-----> after
     *                    .mark <----call---------- *
     *                       * ------forward--------------------->.mark
     * </pre>
     */
    @Test
    public void proxyShouldBeWeaved() {
        Class<?> aspect = MarkTargetAtAspect.class;
        Target mock = mock(Target.class);

        Target proxy = createWeavedProxy(mock, aspect);
        proxy.theMethod();

        verify(mock).markWeaved(aspect); // Call by the advice and forwarded to the mock
    }

    @Test
    public void canWeaveMockOfInterfaces() {
        Class<?> anAspect = MarkTargetAtAspect.class;
        Target mock = mock(Target.class);

        Target proxy = createWeavedProxy(mock, anAspect);
        proxy.theMethod();
    }

    @Test
    public void canWeaveMockOfConcreteClasses() {
        Class<?> anAspect = MarkTargetAtAspect.class;
        Target mock = mock(ConcreteTarget.class);

        Target proxy = createWeavedProxy(mock, anAspect);
        proxy.theMethod();
    }

    @Test
    public void shouldLeaveOriginalMockUnweaved() {
        Class<?> anAspect = MarkTargetAtAspect.class;
        Target mock = mock(ConcreteTarget.class);

        createWeavedProxy(mock, anAspect);
        mock.theMethod(); // Call on the original mock not on the proxy

        verify(mock, never()).markWeaved(any(Class.class));
        verify(mock).theMethod();
    }

    // CHECKSTYLE:OFF (the test is long because it's the goal of the test to create many mocks)

    @Test
    public void canCreateManyIndependantProxiesInTheSameTest() {
        Class<?> firstAspect = MarkTargetAtAspect.class;
        Class<?> secondAspect = SecondMarkTargetAtAspect.class;
        Target firstMock = mock(Target.class);
        Target secondMock = mock(Target.class);
        Date otherMock = mock(Date.class);

        createWeavedProxy(firstMock, firstAspect).theMethod();
        createWeavedProxy(firstMock, firstAspect).theMethod();
        createWeavedProxy(firstMock, secondAspect).theMethod();
        createWeavedProxy(secondMock, firstAspect).theMethod();
        createWeavedProxy(otherMock, firstAspect).getTime();

        verify(firstMock, times(2)).markWeaved(firstAspect);
        verify(firstMock, times(1)).markWeaved(secondAspect);
        verify(secondMock).markWeaved(firstAspect);
        verify(otherMock).getTime();
    }

    // CHECKTYLE:ON

    @Test(expected = NotAnAspect.class)
    public void canNotWeaveNonAspect() {
        Target mock = mock(Target.class);
        createWeavedProxy(mock, OtherClass.class); // Throws
    }

    @Test(expected = NotAConcreteAspect.class)
    public void canNotWeaveAbstractAspect() {
        Target mock = mock(Target.class);
        createWeavedProxy(mock, AbstractAspect.class); // Throws
    }

}
