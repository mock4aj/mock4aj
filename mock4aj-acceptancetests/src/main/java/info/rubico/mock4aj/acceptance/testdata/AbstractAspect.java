package info.rubico.mock4aj.acceptance.testdata;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * An abstract aspect.
 */
@Aspect
public abstract class AbstractAspect {

    @Pointcut
    public abstract void abstractPc();

}
