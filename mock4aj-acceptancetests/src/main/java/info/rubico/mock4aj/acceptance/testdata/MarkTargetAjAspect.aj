package info.rubico.mock4aj.acceptance.testdata;

/**
 * A simple aspect that call {@link Target#markWeaved(Class)} after the execution of
 * {@link Target#theMethod()}
 */
public aspect MarkTargetAjAspect {

    pointcut theMethodExecInTarget(Target t) : 
        execution(* Target.theMethod(..))&&
        this(t);

    before(Target t) : theMethodExecInTarget(t) {
        t.markWeaved(this.getClass());
    }
}
