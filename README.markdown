This test project contains the follow dependencies: Scala 2.9.1, Akka 2.0, spray-server 1.0-M1, Jetty 8.1.0, SBT 0.11.2

The goal is to have a RESTful spray-server handing off messages to an actor in the WorkServerActorSystem. To do this I thought to start up the WorkerServerActorSystem first. Next, I'd fire up the spray-server where the HelloService trait contains a few lines of code to get a handle to an actor in the WorkerServerActorSystem.

I've verified that the Master-Slave Actors system works on its own. It can be verified by performing step 1), skipping 2), and executing steps 3) and 4). Work packets should be sent to the remote Workers up to a certain number, then quit.

Order of operations: 

1) start WorkServerActorSystem in one terminal (sbt console)

    run WorkServerSys 2552 (choose option 1)

2) fire up spray-server in a separate terminal (sbt console)

    container:start

3) if 1) and 2) went smoothly, let's add some remote WorkerActorSystems to the mix..in new terminals of course.. (sbt console)

    run WorkerSys1 2553 (choose option 2)

4) repeat step 3) in a new terminal with "WorkerSys2 2554"