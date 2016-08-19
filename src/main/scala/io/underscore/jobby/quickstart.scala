package io.underscore.jobby

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model._
import com.google.api.services.sheets.v4.Sheets

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Arrays
import java.util.List

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}

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
  def main(args: Array[String]): Unit = {

    val spreadsheetId = "1UN12o-LP3EFFw5VjeiP27om99_TmuFs3Pm9O1isinxY"
    val range = "Form Responses 1!A2:K"

    val response : Try[ValueRange] = 
      GoogleAuth().authorize.flatMap(Service.sheets).flatMap { service => 
        Try { 
          service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
          }
        }


    response match {
      case Success(range) => 
        val values : Seq[List[Object]] = range.getValues().asScala
          if (values == null || values.size == 0) {
              println("No data found.")
          } else {
            for (row <- values) { System.out.println(row) }
          }
      case Failure(ex) => println(ex)
    }
  }
}
