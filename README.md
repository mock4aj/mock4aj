![Mock4Aj](https://raw2.github.com/mock4aj/mock4aj/master/logo/mock4aj_lt.png)

mock4aj
=======

Testing aspects (AOP) with Mock Objects
  

---

Mock4Aj is an easy to use framework to help developers to **test their aspects** (with AspectJ and Java for now) using Mock Objects.

It offers an expressive syntax to **verify pointcuts matching** by testing the effects of an **isolated aspect** on virtual classes (Mocks) within a configurable context.

Mock4Aj? is not a virtual mocking framework and doesn't create mocks. It allows the **weaving of virtual mocks** and the **simulation of execution contexts** to unit test aspects (pointcuts and advices).

With Mock4Aj you can:

- Select which aspect to weave into a Mock (test in isolation)
- Weave into generated objects like Mocks
- Simulate calls to a weaved object
- Simulate a call from a fictitious context to test the aspect against it (ex.: call from a class named X implementing Y)

All that can be done

- with a simple syntax
- without breaking the IDE support (auto-completion, refactoring, type checking, ...).
- in **pure Java** (no agent, no special compiler, ...)

---

# Examples

```java
@Aspect
public class HouseAutomationAspect {

	@Pointcut("execution(void Building.leave())")
	protected void leavingABuilding() {}

	@After("leavingABuilding()")
	public void saveEnergyOnLeaving(JoinPoint joinPoint) {
		Building building = (Building) joinPoint.getThis();
		building.turnOnEnergySaving();
	}

}

@Test
public void shouldTurnEnergySavingOnAfterLeaving() {
    Building houseMock = mock(Building.class); // Mock (using Mockito)
    Building houseWeavedMock = createWeavedProxy(houseMock, HouseAutomationAspect.class); // Weave

    houseWeavedMock.leave(); // Execution should be matched by the aspect

    verify(houseMock).turnOnEnergySavingByAspect(); // Expected effect of the Aspect
}
```

```java
@Aspect
public class LeavingAspect {

	@Pointcut("call(void *.leave())")
	protected void leavingAnything() {}

  ...

}

@Test
public void shouldTurnEnergySavingOnLeavingCall() {
    CallContext context = callContext()
    context.withAspect(LeavingAspect.class);
    context.from(fakeSourceClass("NonExistingName").implementing(SomeInterface.class));
  
    call(targetMock, context).leave();
    
    ...
}
``` 


# How to use it

## Weave my Mock

1. Create a mock with your favorite Mocking Framework (we use Mockito here)

  ```
  Building houseMock = mock(Building.class);
  ```

2. Weave your mock with the Aspect to be tested (in isolation)

  ```
  Building houseWeavedMock = createWeavedProxy(houseMock, HouseAutomationAspect.class);
  ```

3. Do your test as usual....

  When: the method House.leave() is called on the mock
      
  ```java
  houseWeavedMock.leave();
  ```

  Then: the aspect should have turn on the energy saving (it should call turnOnEnergySaving when applied)
  
  ```java
  verify(houseMock).turnOnEnergySaving();
  ```

## Simulate a call
Sometimes, a pointcut considers who is calling the targeted method. So you want to be able to simulate a call coming from a given origin to be able to verify if the pointcut matches only when it should.

1. Given a call context: simulate a call coming from TheOrigin

  ```java
  CallContext context = callContext();
  context.withAspect(OnlyTurnOnFromOwner.class);
  context.from(TheOrigin.class);
  ```

2. When: simulate a call to House.leave() on my Mock of House (target)

  ```java
  call(houseMock, context).leave();
  ```

3. Then: should not turn on the energy saving
  ```
  ...
  ```

## Simulate a call from a fictive class
You can also simulate a call coming from a fictive class to see what would match if this class exist.

  ```java
  context.withAspect(OnlyTurnOnFromOwner.class);
  context.from(fakeSourceClass("NonExistingName").implementing(User.class));
  
  call(houseMock, context).leave();
  
  ...
  ```

# Can do more ?

Yes...

See all acceptance tests packaged with the framework:

https://github.com/mock4aj/mock4aj/tree/master/mock4aj-acceptancetests/src/test/java/info/rubico/mock4aj/acceptance
