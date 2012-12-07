package info.rubico.mock4aj.examples.house;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class HouseAutomation {

	@Pointcut("execution(void Building.leave())")
	protected void leavingABuilding() {
	}

	@After("leavingABuilding()")
	public void saveEnergyOnLeaving(JoinPoint joinPoint) {
		Building building = (Building) joinPoint.getThis();
		building.turnOnEnergySaving();
	}

}
