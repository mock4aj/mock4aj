package info.rubico.mock4aj.acceptance.testdata;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;

/**
 * A simple aspect that call {@link Target#markWeaved(Class)} after the execution of
 * {@link Target#theMethod()}
 */
@Aspect
@SuppressWarnings("unused")
public class SecondMarkTargetAtAspect {

    @Pointcut("execution(* theMethod(..)) && within(Target) && this(t)")
    private void theMethodExec(Target t) { // NOPMD
    }

    @SuppressAjWarnings
    @After("theMethodExec(t)")
    public void afterAMethodInTarget(Target t) { // NOPMD
        t.markWeaved(this.getClass());
    }
}
