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
package org.scalaml.app.chap11

import scala.util.{Try, Success, Failure}
import org.scalaml.reinforcement.qlearning.{QLearning, QLInput, QLConfig}
import org.scalaml.workflow.data.DataSource
import org.scalaml.core.XTSeries
import org.scalaml.core.Types.ScalaMl.DblVector
import org.scalaml.trading.YahooFinancials
import org.scalaml.util.DisplayUtils
import org.scalaml.app.Eval
import org.scalaml.reinforcement.qlearning.QLModel

		 /**
		 * <p><b>Purpose</b>: Singleton to Q-Learning algorithm to extract
		 * best trading policies.</p>
		 * 
		 * @author Patrick Nicolas 
		 * @note Scala for Machine Learning Chapter 11 Reinforcement learning / Q-Learning
		 */
object QLearningEval extends Eval {
	import scala.collection.mutable.{ArrayBuffer, ListBuffer}
	import org.apache.log4j.Logger
	import YahooFinancials._
  
		/**
		 * Name of the evaluation 
		 */
	val name: String = "QLearningEval"
	  
	private val logger = Logger.getLogger(name)
	      
	private val stockPricePath = "resources/data/chap11/IBM.csv"
	private val optionPricePath = "resources/data/chap11/IBM_O.csv"
	private val STRIKE_PRICE = 190.0
	 
	private val MIN_TIME_EXPIRATION = 6
	private val FUNCTION_APPROX_STEP = 3
	private val ALPHA = 0.4
	private val DISCOUNT = 0.6
	private val EPISODE_LEN = 35
	private val NUM_EPISODES = 60
	private val MIN_COVERAGE = 0.35
    
		/** 
		 * <p>Execution of the scalatest for <b>QLearning</b> class.
		 * This method is invoked by the  actor-based test framework function, ScalaMlTest.evaluate</p>
		 * @param args array of arguments used in the test
		 * @return -1 in case error a positive or null value if the test succeeds. 
		 */
	def run(args: Array[String]): Int = { 
		DisplayUtils.show(s"$header Reinforcement learning with Q-learning", logger)
       
		val src = DataSource(stockPricePath, false, false, 1)
		val ibmOption = new OptionModel("IBM", STRIKE_PRICE, src, MIN_TIME_EXPIRATION, 
				FUNCTION_APPROX_STEP)

			// Extract the option for the Q-learning model
		val model = for {
			v <- DataSource(optionPricePath, false, false, 1).extract
			_model <- createModel(ibmOption, v)
		} yield _model
		model.map(m => DisplayUtils.show(s"$name ${m.toString}", logger))
								.getOrElse(DisplayUtils.error(s"$name Failed to create a model", logger))
	}
    
	
	private def createModel(
			ibmOption: OptionModel, 
			oPrice: DblVector): Option[QLModel[Array[Int]]] = {
	  
		val fMap = ibmOption.approximate(oPrice)
      
		val input = new ArrayBuffer[QLInput]
		val profits = fMap.values.zipWithIndex
		profits.foreach(v1 => 
			profits.foreach( v2 => 
				input.append(new QLInput(v1._2, v2._2, v1._1 - v2._1)))
		)
   	      
		val goal = input.maxBy( _.reward).to  
		DisplayUtils.show(s"$name Goal state: ${goal.toString}", logger)
		Try {
			val config = QLConfig(ALPHA, DISCOUNT, EPISODE_LEN, NUM_EPISODES, MIN_COVERAGE, getNeighbors)
			QLearning[Array[Int]](config, fMap.size, goal, input.toArray, fMap.keySet)
		}
		match {
		  case Success(qLearning) => qLearning.getModel
		  case Failure(e) => DisplayUtils.none("QLearningEval.createModel failed", logger, e)
		}
	}
    
		// List the neighbors that are allowed 
	private val RADIUS = 4
	val getNeighbors = (idx: Int, numStates: Int) => {

		def getProximity(idx: Int, radius: Int): List[Int] = {
			val idx_max = if(idx + radius >= numStates) numStates-1 else idx+ radius
			val idx_min = if(idx < radius) 0 else idx - radius
			Range(idx_min, idx_max+1).filter( _ != idx)
									.foldLeft(List[Int]())((xs, n) => n :: xs)
		}
		getProximity(idx, RADIUS).toList
	}
}


object MyApp extends App {
  QLearningEval.run(Array.empty)
}

// ------------------------------------  EOF ----------------------------------