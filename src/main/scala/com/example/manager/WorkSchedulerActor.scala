package com.example.manager

import java.util.concurrent.TimeUnit
import akka.actor.Actor
import akka.util.Duration
import com.example.protocol.ActorMessages.{ SendWork, Delay, TaskRequest }
import akka.event.Logging

class WorkSchedulerActor extends Actor {

  val log = Logging(context.system, this)

  override def receive = {
    case TaskRequest(taskNumber, ctx) => {
      println("WorkSchedulerActor received a TaskRequest, task number: " + taskNumber)
      ctx.fail(cc.spray.http.StatusCodes.BadRequest)
    }
    case Delay(time) => {
      context.system.scheduler.scheduleOnce(Duration.create(time, TimeUnit.SECONDS), sender, SendWork)
    }
    case SendWork => {
      log.info("Received work sending request")
      context.system.scheduler.scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS), sender, SendWork)
    }
  }

}