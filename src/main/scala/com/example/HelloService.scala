package com.example

import cc.spray._
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.example.manager.WorkServerActorSystem

trait HelloService extends Directives {
  
  // attempting to get handle to an actor in my already started WorkServerActorSystem...
  val workServerSystemRef = ActorSystem("WorkServerSys", ConfigFactory.load.getConfig("WorkServerSys"))
  val remoteActor = workServerSystemRef.actorFor("akka://WorkServerSys@127.0.0.1:2552/user/WorkSchedulerActor")
  
  val helloService = {
    path("") {
      
      // at this point I'd like to hand off messages to my remoteActor
      get { _.complete("Say hello to Spray!") }
    }
  }
  
}