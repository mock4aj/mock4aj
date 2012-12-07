package info.rubico.mock4aj.acceptance;

import static org.mockito.Mockito.*;

import info.rubico.mock4aj.acceptance.testdata.MarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.Target;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.codegen.cglib.proxies.CglibWeavedProxyFactory;
import info.rubico.mock4aj.internal.WeavedProxyFacade;
import info.rubico.mock4aj.weavers.aspectj.AspectJWeaver;
import info.rubico.mock4aj.weavers.aspectj.DynamicRuntimeWeavingAdaptor;
import info.rubico.mock4aj.acceptance.testdata.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Before;
import org.junit.Test;

/**
 * <h1>Weave with AspectJ at runtime</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a unit tester, I can weave my aspects into my mocks using the aspectJ weaver implementation so
 * that I can use create Mocks in my test code (dynamic) and choose which aspect to weave into.
 * </p>
 */
public class WeaveWithAspectJAtRuntimeTest {

    private WeavedProxyFacade core;

    @Before
    public void configureCoreAPIWithAspectJWeaver() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        DynamicRuntimeWeavingAdaptor ajWeavingAdapter = new DynamicRuntimeWeavingAdaptor(loader);

        Weaver ajWeaver = new AspectJWeaver(ajWeavingAdapter);

        CglibWeavedProxyFactory proxyFactory = new CglibWeavedProxyFactory();
        core = new WeavedProxyFacade(proxyFactory, ajWeaver);
    }

    @Test
    public void canUseRealDotAjDefinedAspects() {
        Target mock = mock(Target.class);
         // IDE may show an error if you don't have an AspectJ plugin installed.
        Class<?> dotAjAspect = MarkTargetAjAspect.class; 
        core.createWeavedProxy(mock, dotAjAspect).theMethod();

        verify(mock).markWeaved(dotAjAspect);
    }

    @Test
    public void canUseAtAspectDefinedAspects() {
        Target mock = mock(Target.class);
        Class<?> atAspect = MarkTargetAtAspect.class;

        core.createWeavedProxy(mock, atAspect).theMethod();

        verify(mock).markWeaved(atAspect);
    }

    @Test
    public void canUseInnerAspects() {
        Target mock = mock(Target.class);
        Class<?> innerAspect = InnerAspect.class;

        core.createWeavedProxy(mock, innerAspect).theMethod();

        verify(mock).markWeaved(innerAspect);
        verify(mock, never()).theMethod();
    }

    @Aspect
    public static class InnerAspect {

        @Pointcut("execution(void *..Target.theMethod(..)) && !within(*Test)")
        public void anyTheMethod() {
        }

        @Around("anyTheMethod()")
        public void overrideTheMethodAndMark(ProceedingJoinPoint jp) { // NOPMD
            Target target = (Target) jp.getThis();
            target.markWeaved(getClass());
        }
    }

}
