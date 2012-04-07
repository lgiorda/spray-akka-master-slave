package com.example.protocol
import cc.spray.RequestContext

object ActorMessages {

  case class StartWorker(actorPath: String)

  case class StopWorker(actorPath: String)

  case class Task(taskNumber: Int)

  case class TaskFinished(taskNumber: Int)

  case object Begin

  case object Stop

  case object SendWork

  case class TaskRequest(taskNumber: Int, ctx: cc.spray.RequestContext)
  
  case class Delay(time: Int)

}