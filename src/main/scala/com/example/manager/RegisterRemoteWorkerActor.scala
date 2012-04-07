package com.example.manager

import java.util.concurrent.TimeUnit
import akka.actor.Actor
import akka.actor.ActorRef
import akka.util.Duration
import com.example.protocol.ActorMessages._
import akka.event.Logging


class RegisterRemoteWorkerActor(jobControllerActor: ActorRef) extends Actor {
    
  val log = Logging(context.system, self)

  override def receive = {
    case StartWorker(actorPath) => {
      println("remote worker actor from " + actorPath + " is requesting to do work")
      jobControllerActor ! StartWorker(actorPath)
    }
    case _ => {
      println("received an unknown message type")
    }
  }
}