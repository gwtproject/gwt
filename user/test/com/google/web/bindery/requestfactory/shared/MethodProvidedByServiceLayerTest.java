package com.google.web.bindery.requestfactory.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class MethodProvidedByServiceLayerTest extends GWTTestCase {

  public interface Factory extends RequestFactory {
    Context context();
  }

  /**
   * Note: the {@link SkipInterfaceValidation} is put on each method to test
   * that it's actually looked up at that location (it was searched on the
   * RequestContext only at some point).
   */
  @Service(ServiceImpl.class)
  public interface Context extends RequestContext {
    @SkipInterfaceValidation
    Request<String> missingDomainMethod(String string);

    // mapped to SimpleFoo#echo(SimpleFoo)
    @SkipInterfaceValidation
    Request<Proxy> missingDomainType(Proxy proxy);

    // mapped to SimpleFoo#persistAndReturnSelf
    @SkipInterfaceValidation
    InstanceRequest<Proxy, Proxy> missingDomainTypeInstanceMethod();
  }

  @SkipInterfaceValidation
  @ProxyForName("does.not.exist")
  public interface Proxy extends EntityProxy {
  }

  public static class ServiceImpl {
    // Note: the methods are all provided by
    // MethodProvidedByServiceLayerJreTest.Decorator
  }

  private static final int TEST_DELAY = 5000;

  private Factory factory;

  @Override
  public String getModuleName() {
    return "com.google.web.bindery.requestfactory.gwt.RequestFactorySuite";
  }

  protected Factory createFactory() {
    Factory toReturn = GWT.create(Factory.class);
    toReturn.initialize(new SimpleEventBus());
    return toReturn;
  }

  public void testMissingDomainMethod() {
    delayTestFinish(TEST_DELAY);
    Context ctx = context();
    ctx.missingDomainMethod("foo").fire(new Receiver<String>() {
      @Override
      public void onSuccess(String response) {
        assertEquals("foo", response);
        finishTest();
      }
    });
  }

  public void testMissingDomainType() {
    delayTestFinish(TEST_DELAY);
    Context ctx = context();
    final Proxy proxy = ctx.create(Proxy.class);
    ctx.missingDomainType(proxy).fire(new Receiver<Proxy>() {
      @Override
      public void onSuccess(Proxy response) {
        // we only check that the call succeeds
        finishTest();
      }
    });
  }

  public void testMissingDomainTypeInstanceMethod() {
    delayTestFinish(TEST_DELAY);
    Context ctx = context();
    final Proxy proxy = ctx.create(Proxy.class);
    ctx.missingDomainTypeInstanceMethod().using(proxy).fire(new Receiver<Proxy>() {
      @Override
      public void onSuccess(Proxy response) {
        // we only check that the call succeeds
        finishTest();
      }
    });
  }

  @Override
  protected void gwtSetUp() {
    factory = createFactory();
  }

  private Context context() {
    return factory.context();
  }
}
