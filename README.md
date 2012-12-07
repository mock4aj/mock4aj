![Mock4Aj](http://mock4aj.googlecode.com/files/mock4aj_lt.png)

mock4aj
=======

Testing aspects (AOP) with Mock Objects

Documentation (draft):
  https://code.google.com/p/mock4aj/
  

---

Mock4Aj? is an easy to use framework to help developers to test their aspects (with AspectJ and Java for now) using Mock Objects.

It offers an expressive syntax to verify pointcuts matching by testing the effects of an isolated aspect on virtual classes (Mocks) within a configurable context.

Mock4Aj? is not a virtual mocking framework and doesn't create mocks. It allows the weaving of virtual mocks and the simulation of execution contexts to unit test aspects (pointcuts and advices).

With Mock4Aj? you can:

Select which aspect to weave into a Mock (test in isolation)
Weave into generated objects like Mocks
Simulate calls to a weaved object
Simulate a call from a fictitious context to test the aspect against it (ex.: call from a class named X implementing Y)
All that can be done

with a simple syntax
without breaking the IDE support (auto-completion, refactoring, type checking, ...).
in pure Java (no agent, no special compiler, ...)