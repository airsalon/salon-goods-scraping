name := "salon-goods-scraping"

version := "1.0"

scalaVersion := "2.12.2"

import AssemblyKeys._ // put this at the top of the file

assemblySettings

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.1"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.4"
