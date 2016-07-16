package services

import java.util.concurrent.atomic.AtomicInteger
import javax.inject._

/**
 * This trait demonstrates how to create a component that is injected
 * into a controller. The trait represents a counter that returns a
 * incremented number each time it is called.
 */
trait Counter {
  def nextCount(): Int
}

case object AtomicCounter extends Counter {
  private val atomicCounter = new AtomicInteger()
  override def nextCount(): Int = atomicCounter.getAndIncrement()
}
