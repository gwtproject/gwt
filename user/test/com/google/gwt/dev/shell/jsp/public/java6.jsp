<%@ page session="false" contentType="application/json" %>
<%!
// Test Java 6 source support by using @Override on a method coming from an interface
interface IFace {
  void method();
}

class Cls implements Iface {
  @Override
  public void method() { }
}
%>
<%
out.println("OK");
%>
