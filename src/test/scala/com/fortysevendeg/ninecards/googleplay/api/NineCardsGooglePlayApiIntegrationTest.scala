package com.fortysevendeg.ninecards.googleplay.api

import com.fortysevendeg.ninecards.googleplay.TestConfig
import com.fortysevendeg.ninecards.googleplay.domain.Domain._
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._
import cats.data.Xor

import scalaz.concurrent.Task
import com.fortysevendeg.extracats._

import com.fortysevendeg.ninecards.googleplay.ninecardsspray._
import com.fortysevendeg.ninecards.googleplay.service.free.interpreter.Http4sTaskInterpreter._
import com.fortysevendeg.ninecards.googleplay._

class NineCardsGooglePlayApiIntegrationTest extends Specification with Specs2RouteTest with TestConfig {

  /*
   * Reasons this test suite may fail:
   *   * The X-Android-ID and X-Google-Play-Token parameters are no longer accepted by Google Play.
   *   * The packages in the field validPackages are no longer in the Google Play Store (Spain, set by the X-Android-Market-Localization header)
   *       - The URL for checking the validity is https://play.google.com/store/apps/details?id=$PACKAGE&hl=es_ES
   */

  val requestHeaders = List(
    RawHeader("X-Android-ID", androidId.value),
    RawHeader("X-Google-Play-Token", token.value),
    RawHeader("X-Android-Market-Localization", localization.value)
  )

  val route = new NineCardsGooglePlayApi {
    override def actorRefFactory = system
  }.googlePlayApiRoute[Task]

  val validPackages = List("air.fisherprice.com.shapesAndColors", "com.rockstargames.gtalcs", "com.ted.android")
  val invalidPackages = List("com.package.does.not.exist", "com.another.invalid.package")
  val allPackages = validPackages ++ invalidPackages

  "Calling a propertly wired NineCardsGoogleApi class" should {
    "Successfully connect to Google Play and give a response for a single package" in {
      Get(s"/googleplay/package/${validPackages.head}") ~> addHeaders(requestHeaders) ~> route ~> check {
        status must_=== OK

        val docid = decode[Item](responseAs[String]).map(_.docV2.docid)

        docid must_=== Xor.right(validPackages.head)
      }
    }

    "Successfully connect to Google Play and give a response for a list of packages" in {
      Post(s"/googleplay/packages/detailed", PackageListRequest(allPackages)) ~> addHeaders(requestHeaders) ~> route ~> check {
        status must_=== OK

        val response = decode[PackageDetails](responseAs[String]).map {
          case PackageDetails(errors, items) => (errors.toSet, items.map(_.docV2.docid).toSet)
        }

        response must_=== Xor.right((invalidPackages.toSet, validPackages.toSet))
      }
    }
  }
}
