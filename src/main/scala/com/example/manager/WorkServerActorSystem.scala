package com.example.manager

import akka.kernel.Bootable
import akka.remote.RemoteLifeCycleEvent
import com.typesafe.config.{ Config, ConfigFactory }
import akka.dispatch.{ PriorityGenerator, UnboundedPriorityMailbox }
import akka.actor.{ PoisonPill, Address, Props, ActorSystem }
import com.example.protocol.ActorMessages.Begin
import akka.event.Logging

class WorkServerActorSystem extends Bootable {


  def startup = {}
  
  println("started up WorkServerActorSystem")
  val system = ActorSystem("WorkServerSys", ConfigFactory.load.getConfig("WorkServerSys"))

  val workSchedulerActor = system.actorOf(Props[WorkSchedulerActor], "WorkSchedulerActor")
  val jobControllerActor = system.actorOf(Props(new JobControllerActor(workSchedulerActor)), "JobControllerActor")
  val registerRemoteWorkerActor = system.actorOf(Props(new RegisterRemoteWorkerActor(jobControllerActor)), "RegisterRemoteWorkerActor")

  workSchedulerActor ! Begin

  override def shutdown = {
    println("SHUTTING DOWN the ServerActorSystem...goodbye!")
  }
}

class MyPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(

  PriorityGenerator {
    case Address => 0
    case PoisonPill => 3
    case otherwise => 1
  })


object WorkServerApp {
  def main(args: Array[String]) {
    new WorkServerActorSystem
  }
}
