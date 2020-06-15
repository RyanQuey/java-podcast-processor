package kafkaMains;

import kafkaHelpers.Consumers;

public class RunSearchPerTermMain extends KafkaMain {

  /////////////////////////////////////
  // static vars

  private static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("run a search query for each term in topic");
    Consumers.initializeQueryTermConsumer();
  }

  ///////////////////////////////////////
  // private static methods
  
}
