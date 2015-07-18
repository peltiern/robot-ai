package fr.peltier.nicolas.robot.ai.test.webservice;

import javax.jws.WebService;

//Service Implementation
@WebService(endpointInterface = "fr.peltier.nicolas.robot.ai.test.webservice.HelloWorld")
public class HelloWorldImpl implements HelloWorld{

  @Override
  public String getHelloWorldAsString(String name) {
      return "Hello World JAX-WS " + name;
  }

}
