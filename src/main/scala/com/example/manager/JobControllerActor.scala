package com.example.manager

import akka.event.Logging
import collection.immutable.HashMap
import com.example.worker.WorkerActor
import akka.actor._
import com.example.protocol.ActorMessages._
import akka.routing.{ RoundRobinRouter, RemoteRouterConfig }

class JobControllerActor(inWorkScheduler: ActorRef) extends Actor {

  val workSchedulerActor = inWorkScheduler
  val log = Logging(context.system, this)
  var count = 0
  // this will be used to deploy remote routees
  var workerRouterActor: ActorRef = _
  // this will be updated as our WorkerActors register and unregister
  var workerAddressMap = new scala.collection.mutable.HashMap[String, Address]

  override def receive = {

    case StartWorker(actorPath) => {
      log.info("Received worker registration request")
      addWorkerRoute(actorPath)
      workSchedulerActor ! SendWork
    }

    case SendWork => {
      log.info("About to send work packets to workers")
      if (workerRouterActor != null) {
        workerAddressMap foreach { _ =>
          count += 1
          workerRouterActor ! Task(count)
        }
        log.info("Work packets send up to ->" + count)
        // let the WorkScheduler know that you finished sending work packets
        workSchedulerActor ! SendWork
      } else {
        log.info("No workers registered as of right now!")
        // tell WorkScheduler to wake me up in 5 seconds
      }
    }

    case TaskFinished(taskNumber) => {
      log.info("task finished -> " + taskNumber)
    }

    case StopWorker(actorPath) => {
      log.info("Worker requesting shutdown -> " + actorPath)
      removeWorkerRoute(actorPath)
    }

  }

  /**
   *
   * @param address  address of the remote WorkerActor
   */
  private def addWorkerRoute(address: String) = {

    log.info("Adding a new worker to the JobControllerActor")

    if (workerRouterActor != null) {
      workerAddressMap.foreach { _ =>
        workerRouterActor ! Stop
      }
    }

    workerAddressMap.put(address, AddressFromURIString.parse(address))
    var workerAddresses = workerAddressMap.values.toSeq
    workerRouterActor = context.system.actorOf(Props[WorkerActor].withRouter(RemoteRouterConfig(
        RoundRobinRouter(workerAddresses.length), workerAddresses))
       	)

  }

  /**
   *
   * @param address  address of the remote WorkerActor
   */
  private def removeWorkerRoute(address: String): Unit = {

    if (workerRouterActor != null) {
      if (!workerAddressMap.contains(address)) {
        return
      }
    }

    log.info("Processing Worker shutdown request -> " + address)

    if (workerRouterActor != null) {
      workerAddressMap foreach { _ =>
        if (!workerRouterActor.isTerminated) {
          workerRouterActor ! Stop
        }
      }
    }

    if (workerAddressMap.size > 0) {
      workerAddressMap.remove(address)
    }

    if (workerAddressMap.size > 0) {
      var workerAddresses = workerAddressMap.values.toSeq
      workerRouterActor = context.system.actorOf(Props[WorkerActor].withRouter(RemoteRouterConfig(
          RoundRobinRouter(workerAddresses.length), workerAddresses))
          )
    }
  }
}