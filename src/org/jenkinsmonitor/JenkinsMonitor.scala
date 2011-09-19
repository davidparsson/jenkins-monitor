package org.jenkinsmonitor

import scala.xml.XML
import scala.xml.Node
import scala.collection.mutable.ListBuffer

object JenkinsMonitor {

  var VIEW_URL = "http://deadlock.netbeans.org/hudson/"
  var XML_SUFFIX = "api/xml?tree=jobs[color,name,lastBuild[number,timestamp],lastStableBuild[number,timestamp],lastSuccessfulBuild[number,timestamp]]"

  def main(args: Array[String]) {
    var url = VIEW_URL
    if (args.length > 0) {
      url = args(0)
    }

    val data = XML.load(new java.net.URL(url + XML_SUFFIX))

    var jobs = new JobList()

    println("URL: ")
    println(url + XML_SUFFIX)
    println()

    println("All jobs:")
    for (val jobXml <- data \ "job") {
      val job = new Job(jobXml)
      jobs.add(job)
      println(job)
    }

    println()
    println("Most severe status:")
    println(jobs.mostSevereStatus)

  }
}

object BuildStatus extends Enumeration {
  type BuildStatus = Value
  val Disabled = Value(0)
  val Aborted = Value(1)
  val Stable = Value(2)
  val Unstable = Value(3)
  val Broken = Value(4)

  def getStatusForColor(color: String) = {
    color match {
      case "blue" | "blue_anime" => Stable
      case "yellow" | "yellow_anime" => Unstable
      case "red" | "red_anime" => Broken
      case "aborted" | "aborted_anime" => Aborted
      case _ => Disabled
    }
  }
}

class Job(jobXml: Node) {
  val name = (jobXml \ "name").text
  val buildNumber = (jobXml \ "lastBuild" \ "number").text
  val color = (jobXml \ "color").text
  val status = BuildStatus.getStatusForColor(color)

  override def toString() =
    name + " #" + buildNumber + ": " + status
}

class JobList() {
  var jobs = ListBuffer[Job]()
  var mostSevereStatus = BuildStatus.Disabled

  def add(job: Job) {
    jobs += job
    if (job.status > mostSevereStatus) {
      mostSevereStatus = job.status
    }
  }

  def getJobs() = {
    jobs.toList
  }
}
