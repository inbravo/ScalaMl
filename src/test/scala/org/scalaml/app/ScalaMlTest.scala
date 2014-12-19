/**
 * Copyright (c) 2013-2015  Patrick Nicolas - Scala for Machine Learning - All rights reserved
 *
 * The source code in this file is provided by the author for the sole purpose of illustrating the 
 * concepts and algorithms presented in "Scala for Machine Learning". It should not be used to build commercial applications. 
 * ISBN: 978-1-783355-874-2 Packt Publishing.
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * Version 0.97.3
 */
package org.scalaml.app

import scala.annotation.switch
import org.scalatest.FunSuite
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure, Properties}
import scala.concurrent.duration.Duration
import org.scalaml.util.DisplayUtils
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent._
import org.scalatest.FunSuite
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.time._





	/**
		 * <p>Generic template for the scalatest invocation.</p>
		 * @author Patrick Nicolas
		 * @since July, 13, 2014
		 * @note Scala for Machine Learning
		 */
trait ScalaMlTest extends FunSuite with ScalaFutures {
	val chapter: String
	
	private val MAX_EXECUTION_TIME: Int = 40
	implicit val patience = PatienceConfig(timeout = Span(MAX_EXECUTION_TIME, Seconds), 
			interval = Span(250, Millis))
			
		/**
		 * <p>Trigger the execution of a Scala test for a specific method and set of arguments.</p>
		 * @param args argument for the Scala test
		 * @param method Name of the method to be tested.
		 */
	def evaluate(eval: Eval, args: Array[String] = Array.empty): Boolean = {
  	val f: Future[Int] = future { eval.run(args) }
  		
  	whenReady(f) {
  		result => {
  			assert(result >=0, "OK")
  			result >= 0
  		}
  	}
	}
}

		/**
		 * <p>Generic trait to name and execute a test case using Scalatest</p>
		 * @author Patrick Nicolas
		 * @since July, 13, 2014
		 * @note Scala for Machine Learning
		 */
trait Eval {
  		/**
		 * Name of the evaluation 
		 */
	val name: String
		/**
		 * <p>Execution of scalatest case.</p>
		 */
	def run(args: Array[String]): Int
	
	protected def header: String = {
		AllTests.count += 1
		s"\n\n *****  test#${AllTests.count} $name"
	}
}

/*
object Eval {
	var count: Int = _
	def testCount: String = {
		count += 1
		String.valueOf(count)
	}
}
* 
*/



		/**
		 * Singleton that defines the actor context for
		 * all the tests. In the case of sbt test:run execution, the
		 * context is initialized only once before execution of all the tests.
		 * and shutdown after the execution of all the tests.
		 */
/*
object TestContext  { 
	import org.apache.log4j.Logger
	private val logger = Logger.getLogger("TestContext")
	

		
	private val ELAPSE_TIME = 4000
	private val CONFIGURATION = "Recommended SBT/JVM configuration:\n-Xmx4096 (or higher)\n" +
			" -XX:MaxPermSize=512m (or higher)\n -XX:ReservedCodeCacheSize=256m (or higher)\n" +
			s"Context:\nUser:${Properties.userName}, OS:${Properties.osName}"
	
		/**
		 * Method to validate the version of Scala and Java JDK used.
		 */
	def init: Unit = {
		DisplayUtils.show(CONFIGURATION, logger)
		if( !Properties.isWin && !Properties.isMac)
			DisplayUtils.show("The library has not be tested for this Operating System", logger)
			
		DisplayUtils.show(s"Java version: ${Properties.javaVersion}", logger)
		if(!Properties.isJavaAtLeast("1.7"))
			DisplayUtils.show("Incompatible version of Java, should be 1.7 or later", logger)
			
		val scalaVersion = Properties.versionNumberString
		
		DisplayUtils.show(s"Scala version: $scalaVersion", logger)
		(scalaVersion.charAt(2): @switch) match {
			case '9' => DisplayUtils.show("Scala version should be 2.10.2 or higher", logger)
			case '1' => {
				(scalaVersion.charAt(3): @switch) match {
					case '0' => DisplayUtils.show("Compatible Akka version should be 2.2.4 or lower", logger)
					case '1' => DisplayUtils.show("Compatible Akka version should be 2.3.4 or higher", logger)
				}
			}
			case _ => DisplayUtils.show("Could not initialize", logger)
		}
		Eval.count = 0
	}
	
		/**
		 * Shutdown in-conditionally the Akka actor system, after a
		 * specific amount of time. This function is called by test:run
		 */
	def shutdownAll: Unit = {
		Thread.sleep((ELAPSE_TIME<<1))
	//	actorSystem.shutdown
		DisplayUtils.show(s"test:run completed after ${Eval.count +1} tests", logger)
	}
	
		/**
		 * Shutdown the Akka actor system for individual scalatest
		 * @return 0 for success
		 */
	def shutdown: Int = {
		Thread.sleep(ELAPSE_TIME)
//		if( !allRuns )
//			actorSystem.shutdown
		0
	}
}
* 
*/


// --------------------------  EOF -------------------------------