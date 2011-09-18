package org.jenkinsmonitor

import scala.xml.XML
import scala.xml.Node

object JenkinsMonitor {

  var VIEW_URL = "http://deadlock.netbeans.org/hudson/"
  var XML_SUFFIX = "api/xml?tree=jobs[color,name,lastBuild[number,timestamp],lastStableBuild[number,timestamp],lastSuccessfulBuild[number,timestamp]]"

  def main(args: Array[String]) {
    val data = XML.load(new java.net.URL(VIEW_URL + XML_SUFFIX))

    for (val job <- data \ "job") {
      println((job \ "name").text + " #" + (job \ "lastBuild" \ "number").text + ": " + (job \ "color").text)
    }

  }
}