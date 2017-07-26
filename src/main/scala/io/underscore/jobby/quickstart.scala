package io.underscore.jobby

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model._
import com.google.api.services.sheets.v4.Sheets

import java.io.InputStream
import java.io.InputStreamReader
import java.util.Arrays

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}
/**
 * This is the entry point for running a job conversion.
 * It is the Google example code base modified to process jobs.
 * It is not well named or structured: feel free to improve it.
 */
trait GoogleConfig {
  /** Global instance of the JSON factory. */
  val JSON_FACTORY = JacksonFactory.getDefaultInstance()

  /** Global instance of the HTTP transport. */
  val HTTP_TRANSPORT : HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
}

case class GoogleAuth(val clientSecretsPath: String = "/client_secret.json") extends GoogleConfig {

  /** Global instance of the scopes required by this quickstart.
   *
   * If modifying these scopes, delete your previously saved credentials
   * at ~/.credentials/sheets.googleapis.com-java-quickstart
   */
  val SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY)

  /** Directory to store user credentials for this application. */
  val DATA_STORE_DIR = new java.io.File( System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart")

  /** Global instance of the {@link FileDataStoreFactory}. */
  val DATA_STORE_FACTORY : FileDataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR)

  def authorize(): Try[Credential] = Try {
    // Load client secrets.
    val in : InputStream = classOf[GoogleAuth].getResourceAsStream(clientSecretsPath)
    val clientSecrets : GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))

    // Build flow and trigger user authorization request.
    val flow : GoogleAuthorizationCodeFlow =
            new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build()
    val credential : Credential = new AuthorizationCodeInstalledApp( flow, new LocalServerReceiver()).authorize("user")
    System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath())
    credential
  }
}

object Service extends GoogleConfig {

  /** Application name. */
  val APPLICATION_NAME = "Google Sheets API Java Quickstart"

  /**
   * Build and return an authorized Sheets API client service.
   * @return an authorized Sheets API client service
   * @throws IOException
   */
  def sheets(credential: Credential) : Try[Sheets] = Try {
    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build()
  }
}

object Main {

  import java.time.{Instant, Period }
  val lookback = Instant.now minus Period.ofDays(24)

  import java.nio.file.Path

  def main(args: Array[String]): Unit = args match {
    case Array(spreadsheetId, range) =>
      fetch(spreadsheetId, range) match {
        case Success(range) =>
          import Read._, USDateReader._
          val jobs: List[Try[Job]] = asScala(range).map(Read.as[Job])
          System.err.println("Failures: "+jobs.collect{case Failure(err) => err})
          val results: List[Try[Path]] = jobs.collect{
            case Success(job) if job.timestamp isAfter lookback => IO.write(job)
          }
          results.foreach(println)

        case Failure(err)   => err.printStackTrace()
      }
    case _ =>
      println("Usage: run spreadsheet-id cell-range")
  }

  def fetch(spreadsheetId: String, range: String): Try[ValueRange] =
    GoogleAuth().authorize.flatMap(Service.sheets).flatMap { service =>
      Try { service.spreadsheets().values().get(spreadsheetId, range).execute() }
    }

  def asScala(range: ValueRange): List[List[String]] =
    range.getValues().asScala.toList.map(row => row.asScala.toList.map(_.toString))

}
