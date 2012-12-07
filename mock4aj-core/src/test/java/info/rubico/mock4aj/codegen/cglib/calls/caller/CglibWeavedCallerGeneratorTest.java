package info.rubico.mock4aj.codegen.cglib.calls.caller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallSource;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.codegen.cglib.transformations.CglibWeavingGeneratorStrategy;
import info.rubico.mock4aj.internal.calls.CallTargetByReflection;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CglibWeavedCallerGeneratorTest {

    private CglibWeavedCallerGenerator generator;

    private CallTargetByReflection dateGetTimeTarget;
    private MethodCallerBytecodeGenerator generatorMock;
    private MethodCaller generatedCallerMock;
    private CallContext contextMock;
    private CglibWeavingGeneratorStrategy weavingStrategyMock;
    private Weaver weaverMock;
    private CallSource sourceMock;

    @Before
    public void setupWeavedCallerFactory() throws NoSuchMethodException {
        weaverMock = mock(Weaver.class);
        sourceMock = mock(CallSource.class);

        contextMock = mock(CallContext.class);
        when(contextMock.getConfiguredWeaver()).thenReturn(weaverMock);
        when(contextMock.getConfiguredCallSource()).thenReturn(sourceMock);

        dateGetTimeTarget = new CallTargetByReflection(new Date(), "getTime");

        generatedCallerMock = mock(MethodCaller.class);
        generatorMock = mock(MethodCallerBytecodeGenerator.class);
        when(generatorMock.create()).thenReturn(generatedCallerMock);

        weavingStrategyMock = mock(CglibWeavingGeneratorStrategy.class);
        when(weavingStrategyMock.getWeaver()).thenReturn(weaverMock);

        setupGeneratorWithMocks();
    }

    private void setupGeneratorWithMocks() {
        generator = new CglibWeavedCallerGenerator() {
            @Override
            protected MethodCallerBytecodeGenerator createGenerator(CallTarget target) {
                return generatorMock;
            }

            @Override
            protected CglibWeavingGeneratorStrategy createWeavingStrategy(CallContext context) {
                return weavingStrategyMock;
            }
        };
    }

    @Test
    public void shouldConfigureTheClassGeneratorWithAWeavingStrategy() {
        generator.generateMethodCaller(dateGetTimeTarget, contextMock);
        verify(generatorMock).setStrategy(weavingStrategyMock);
    }

    @Test
    public void shouldCreateTheWeavingStrategyWithTheContextWeaver() {
        CglibWeavedCallerGenerator newGenerator = new CglibWeavedCallerGenerator();
        CglibWeavingGeneratorStrategy strategy = newGenerator.createWeavingStrategy(contextMock);

        verify(contextMock).getConfiguredWeaver();
        assertSame(strategy.getWeaver(), weaverMock);
    }

    @Test
    public void shouldCreateTheCallerUsingTheClassGenerator() {
        MethodCaller caller = generator.generateMethodCaller(dateGetTimeTarget, contextMock);
        assertSame(caller, generatedCallerMock);
        verify(generatorMock).create();
    }

    @Test
    public void shouldDisableCachingBecauseItDoesntConsiderWeavingTransformation() {
        generator.generateMethodCaller(dateGetTimeTarget, contextMock);
        verify(generatorMock).setUseCache(false);
    }

    @Test
    public void shouldConfigureTheClassGeneratorWithTheContextSource() {
        generator.generateMethodCaller(dateGetTimeTarget, contextMock);
        verify(generatorMock).setSource(sourceMock);
    }
}
